package com.lamfire.filequeue;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lamfire.logger.Logger;

/**
 * 基于内存映射的文件读写Buffer 由于采用了内存映射方式,所以约定单文件长度不超过1GB
 */
public class FileBuffer {

    private static final Logger LOGGER               = Logger.getLogger(FileBuffer.class);
    /**
     * 默认的缓冲区大小 : 4MB
     */
    private static final int    _DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;                   // 4m

    /**
     * 文件的最大SIZE:1GB
     */
    private static final int    _MAX_FILE_LENGTH     = 1024 * 1024 * 1024;                // 1G

    private final Lock          lock                 = new ReentrantLock();

    private MappedByteBuffer    readBuffer           = null;
    private int                 readPosition         = 0;
    private int                 readMapOffset        = 0;

    private MappedByteBuffer    writeBuffer          = null;
    private int                 writePosition        = 0;
    private int                 writeMapOffset       = 0;

    private File                file;
    private RandomAccessFile    raf                  = null;
    private FileChannel         channel;

    private final int           bufferSize;
    private final int           fileMaxLength;

    public FileBuffer(File file) throws IOException {
        this(file, _DEFAULT_BUFFER_SIZE, _MAX_FILE_LENGTH);
    }

    public FileBuffer(File file, int bufferSize) throws IOException {
        this(file, bufferSize, _MAX_FILE_LENGTH);
    }

    public FileBuffer(File file, int bufferSize, int fileMaxLength) throws IOException {
        this.bufferSize = bufferSize;
        this.fileMaxLength = fileMaxLength;
        this.file = file;
        initialize();
    }

    protected synchronized FileChannel getFileChannel() {
        if (this.channel == null) {
            this.channel = getRandomAccessFile().getChannel();
        }
        return this.channel;
    }

    protected synchronized RandomAccessFile getRandomAccessFile() {
        if (this.raf == null) {
            try {
                this.raf = new RandomAccessFile(file, "rwd");
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        return this.raf;
    }

    protected MappedByteBuffer mmap(int postion) {
        try {
            lock.lock();
            int mapLength = bufferSize;
            int remaining = fileMaxLength - postion;
            if (remaining < bufferSize) {
                mapLength = remaining;
            }
            return getFileChannel().map(FileChannel.MapMode.READ_WRITE, postion, mapLength);
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

    protected void unmap(MappedByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        try {
            buffer.force();
            Method cleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
            if (cleanerMethod != null) {
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer, new Object[0]);
                if (cleaner != null) {
                    Method clearMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                    if (cleanerMethod != null) {
                        clearMethod.invoke(cleaner, new Object[0]);
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private synchronized void initialize() throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (raf == null) {
            raf = new RandomAccessFile(file, "rwd");
            channel = raf.getChannel();
        }
    }

    public int getFileMaxLength() {
        return fileMaxLength;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void closeAndDeleteFile() {
        try {
            lock.lock();
            close();
            file.getAbsoluteFile().delete();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 调整读内存映射地址
     * 
     * @param necessary 需要的内存空间
     * @throws java.io.IOException
     */
    void adjustReadMapper(int necessary) throws IOException {
        try {
            lock.lock();
            if ((this.readPosition + necessary) > fileMaxLength) {
                throw new IOException("Read length out of disk space");
            }

            if (this.readBuffer == null) {
                this.readBuffer = mmap(this.readPosition);
                this.readMapOffset = this.readPosition;
                return;
            }

            if (this.readMapOffset + bufferSize < this.readPosition + necessary
                || this.readPosition < this.readMapOffset) {
                unmap(this.readBuffer);
                this.readBuffer = mmap(this.readPosition);
                this.readMapOffset = this.readPosition;
                return;
            }

            if (this.readMapOffset + this.readBuffer.position() != this.readPosition) {
                int postion = this.readPosition - this.readMapOffset;
                this.readBuffer.position(postion);
            }
        } finally {
            lock.unlock();
        }
    }

    public int getFreeWriteSpace() {
        try {
            this.lock.lock();
            return fileMaxLength - this.writePosition;
        } finally {
            lock.unlock();
        }
    }

    public void commit() {
        try {
            this.lock.lock();
            if (this.writeBuffer != null) {
                this.writeBuffer.force();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 调整写内存映射地址
     * 
     * @param necessary 需要的内存空间
     * @throws java.io.IOException
     */
    void adjustWriteMapper(int necessary) throws IOException {
        try {
            lock.lock();
            if ((this.writePosition + necessary) > fileMaxLength) {
                throw new IOException("No more disk space");
            }

            if (this.writeBuffer == null) {
                this.writeBuffer = mmap(this.writePosition);
                this.writeMapOffset = this.writePosition;
                return;
            }

            if (this.writeMapOffset + bufferSize < this.writePosition + necessary
                || this.writePosition < this.writeMapOffset) {
                unmap(this.writeBuffer);
                this.writeBuffer = mmap(this.writePosition);
                this.writeMapOffset = this.writePosition;
                return;
            }

            if (this.writeMapOffset + this.writeBuffer.position() != this.writePosition) {
                int position = this.writePosition - this.writeMapOffset;
                this.writeBuffer.position(position);
            }
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        try {
            this.lock.lock();
            if (this.writeBuffer != null) {
                unmap(this.writeBuffer);
                this.writeBuffer = null;
            }

            if (this.readBuffer != null) {
                unmap(this.readBuffer);
                this.readBuffer = null;
            }

            if (this.channel != null) {
                this.channel.close();
                this.channel = null;
            }

            if (this.raf != null) {
                this.raf.close();
                this.raf = null;
            }
            if (shutdownCloseThread != null) {
                Runtime.getRuntime().removeShutdownHook(shutdownCloseThread);
                shutdownCloseThread = null;
            }

        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

    public File getFile() {
        return file;
    }

    public int getReadPosition() {
        return readPosition;
    }

    public int getWritePosition() {
        return writePosition;
    }

    /**
     * 设置读的起始位置
     * 
     * @param position
     */
    public void setReadPosition(int position) {
        try {
            lock.lock();
            this.readPosition = position;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置写的起始位置
     * 
     * @param position
     */
    public void setWritePosition(int position) {
        try {
            lock.lock();
            this.writePosition = position;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 写入数据
     * 
     * @param bytes
     * @throws java.io.IOException
     */
    public void put(byte[] bytes) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(bytes.length);
            this.writeBuffer.put(bytes);
            this.writePosition += bytes.length;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 写入数据
     *
     * @param bytes
     * @throws java.io.IOException
     */
    public void put(byte[] bytes, int offset, int length) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(length);
            this.writeBuffer.put(bytes, offset, length);
            this.writePosition += length;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 读取数据
     * 
     * @param bytes
     * @throws java.io.IOException
     */
    public void get(byte[] bytes) throws IOException {
        try {
            lock.lock();
            adjustReadMapper(bytes.length);
            this.readBuffer.get(bytes);
            this.readPosition += bytes.length;
        } finally {
            lock.unlock();
        }
    }

    public void put(byte b) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(1);
            this.writeBuffer.put(b);
            this.writePosition++;
        } finally {
            lock.unlock();
        }
    }

    public byte get() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(1);
            byte value = this.readBuffer.get();
            this.readPosition++;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void putInt(int value) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(4);
            this.writeBuffer.putInt(value);
            this.writePosition += 4;
        } finally {
            lock.unlock();
        }
    }

    public void putInt(int position, int value) throws IOException {
        try {
            lock.lock();
            setWritePosition(position);
            putInt(value);
        } finally {
            lock.unlock();
        }
    }

    public int getInt() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(4);
            int value = this.readBuffer.getInt();
            this.readPosition += 4;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public int getInt(int position) throws IOException {
        try {
            lock.lock();
            setReadPosition(position);
            return getInt();
        } finally {
            lock.unlock();
        }
    }

    public void putShort(short value) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(2);
            this.writeBuffer.putShort(value);
            this.writePosition += 2;
        } finally {
            lock.unlock();
        }
    }

    public void putShort(int position, short value) throws IOException {
        try {
            lock.lock();
            setWritePosition(position);
            putShort(value);
        } finally {
            lock.unlock();
        }
    }

    public short getShort() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(2);
            short value = this.readBuffer.getShort();
            this.readPosition += 2;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public short getShort(int position) throws IOException {
        try {
            lock.lock();
            setReadPosition(position);
            return getShort();
        } finally {
            lock.unlock();
        }
    }

    public void putLong(long value) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(8);
            this.writeBuffer.putLong(value);
            this.writePosition += 8;
        } finally {
            lock.unlock();
        }
    }

    public void putLong(int position, long value) throws IOException {
        try {
            lock.lock();
            setWritePosition(position);
            putLong(value);
        } finally {
            lock.unlock();
        }
    }

    public long getLong() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(8);
            long value = this.readBuffer.getLong();
            this.readPosition += 8;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public long getLong(int position) throws IOException {
        try {
            lock.lock();
            setReadPosition(position);
            return getLong();
        } finally {
            lock.unlock();
        }
    }

    public void putFloat(float value) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(4);
            this.writeBuffer.putFloat(value);
            this.writePosition += 4;
        } finally {
            lock.unlock();
        }
    }

    public void putFloat(int position, float value) throws IOException {
        try {
            lock.lock();
            setWritePosition(position);
            putFloat(value);
        } finally {
            lock.unlock();
        }
    }

    public float getFloat() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(4);
            float value = this.readBuffer.getFloat();
            this.readPosition += 4;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public float getFloat(int position) throws IOException {
        try {
            lock.lock();
            setReadPosition(position);
            return getFloat();
        } finally {
            lock.unlock();
        }
    }

    public void putDouble(double value) throws IOException {
        try {
            lock.lock();
            adjustWriteMapper(8);
            this.writeBuffer.putDouble(value);
            this.writePosition += 8;
        } finally {
            lock.unlock();
        }
    }

    public void putDouble(int position, double value) throws IOException {
        try {
            lock.lock();
            setWritePosition(position);
            putDouble(value);
        } finally {
            lock.unlock();
        }
    }

    public double getDouble() throws IOException {
        try {
            lock.lock();
            adjustReadMapper(8);
            double value = this.readBuffer.getDouble();
            this.readPosition += 8;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public double getDouble(int position) throws IOException {
        try {
            lock.lock();
            setReadPosition(position);
            return getDouble();
        } finally {
            lock.unlock();
        }
    }

    public String getFilePath() {
        return this.file.getAbsolutePath();
    }

    void addShutdownHook() {
        if (shutdownCloseThread == null) {
            shutdownCloseThread = new ShutdownCloseThread(this);
            Runtime.getRuntime().addShutdownHook(shutdownCloseThread);
        }
    }

    ShutdownCloseThread shutdownCloseThread = null;

    private static class ShutdownCloseThread extends Thread {

        FileBuffer fileBuffer = null;

        ShutdownCloseThread(FileBuffer fileBuffer) {
            super("FileBuffer Shutdown");
            this.fileBuffer = fileBuffer;
        }

        public void run() {
            if (fileBuffer != null && fileBuffer.raf != null) {
                fileBuffer.shutdownCloseThread = null;
                fileBuffer.commit();
                fileBuffer.close();
                LOGGER.info("[JVM SHUTDOWN] commited and closed.");
            }
        }
    }
}
