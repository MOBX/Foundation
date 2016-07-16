package com.lamfire.filequeue;

import com.lamfire.logger.Logger;
import com.lamfire.utils.CloseOnJvmShutdownHook;
import com.lamfire.utils.Threads;

import java.io.Closeable;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个高性能持久化对列,支持先进先出
 * 
 * @author lamfire
 * 
 */
class FileQueueImpl implements FileQueue,Closeable{
	private static final Logger LOGGER = Logger.getLogger(FileQueueImpl.class);
	private static final int DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;
	private int indexBufferSize = DEFAULT_BUFFER_SIZE;
	private int storeBufferSize = DEFAULT_BUFFER_SIZE;

	private final Lock lock = new ReentrantLock();

	private String dir;
	private String name;

	private MetaBuffer meta;
    private IndexManager indexMgr;
    private DataManager dataMgr;

    private Reader reader;
    private Writer writer;

    private int indexOfLastDeleteStoreFile = 0 ;
    private int indexOfLastDeleteIndexFile = 0;

    private int indexFilePartitionLength;
    private int dataFilePartitionLength;
    private boolean closeOnJvmShutdown = false;

	public FileQueueImpl(String dataDir, String name, int indexBufferSize, int storeBufferSize,int indexFilePartitionLength,int dataFilePartitionLength) throws IOException {
		this.indexBufferSize = indexBufferSize;
        this.indexFilePartitionLength = indexFilePartitionLength;
        this.dataFilePartitionLength = dataFilePartitionLength;
		this.storeBufferSize = storeBufferSize;
		this.dir = dataDir;
		this.name = name;
		File dir = new File(dataDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

        //初始化
		initialize();

        //检查是否为空对列
        if(this.isEmpty() && this.meta.getReadCount() > 0){
            this.renew();
        }
	}

	void initialize() throws IOException {
        try {
            lock.lock();
            if(meta == null){
                meta = new MetaBuffer(MetaBuffer.getMetaFile(dir, name),indexFilePartitionLength,dataFilePartitionLength);
            }

            if(indexMgr == null){
                indexMgr = new IndexManager(this.meta,dir,name);
            }

            if(dataMgr == null){
                dataMgr = new DataManager(this.meta,dir,name);
            }

            if(reader == null){
                reader = new ReaderImpl(this.meta,indexMgr, dataMgr) ;
            }

            if(writer == null){
                writer = new WriterImpl(this.meta,indexMgr, dataMgr);
            }

            indexMgr.setBufferSize(indexBufferSize);
            dataMgr.setBufferSize(storeBufferSize);

            indexOfLastDeleteStoreFile = 0 ;
            indexOfLastDeleteIndexFile = 0;

        } finally {
            lock.unlock();
        }
	}


    /**
     * 定时清理过期文件
     * @param clearExpireFileIntervalSeconds    时间间隔
     */
    protected void enableClearExpireFile(int clearExpireFileIntervalSeconds){
        //启动检查清理过期文件时间任务
        if(clearExpireFileIntervalSeconds > 0){
            Threads.scheduleWithFixedDelay(autoClearExpiredFileMonitor,clearExpireFileIntervalSeconds,clearExpireFileIntervalSeconds, TimeUnit.SECONDS);
        }
    }


	public boolean push(byte[] bytes) {
		try {
			lock.lock();
            writer.write(bytes);
			return true;
		} catch (Exception e) {
			throw new IOError(e);
		} finally {
			lock.unlock();
		}
	}

    public boolean push(byte[] bytes,int offset,int length) {
        try {
            lock.lock();
            writer.write(bytes,offset,length);
            return true;
        } catch (Exception e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

	public  boolean isEmpty() {
		return size() == 0;
	}



    public  byte[] peek() {
		return peek(0);
	}

    public  byte[] peek(int i) {
        if (isEmpty()) {
            return null;
        }
        try {
            lock.lock();
            reader.moveTo(i);
            return reader.read();
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

    public  byte[] pull() {
        if (isEmpty()) {
            return null;
        }
        try {
            lock.lock();
            if(reader.hashMore()){
                reader.moveTo(0);
                byte[] bytes = reader.read();
                reader.moveNext();
                reader.commit();
                return bytes;
            }
            return null;
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

    public void skip(int number){
        if (isEmpty() || number <= 0) {
            return ;
        }
        try {
            lock.lock();
            int i=0;
            while(reader.hashMore()){
                reader.moveNext();
                i++;
                if(i >= number){
                    break;
                }
            }
            reader.commit();
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

	public  long size() {
		try {
			lock.lock();
			return meta.getWriteCount() - meta.getReadCount();
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		try {
			lock.lock();
            deleteAllIndexFiles();
            deleteAllDataFiles();
            meta.clear();
		} catch (IOException e) {
			throw new IOError(e);
		} finally {
			lock.unlock();
		}
	}

    protected void renew(){
        try {
            LOGGER.info("[RENEW] : " + name);
            lock.lock();
            clear();
            this.reader = null;
            this.writer = null;
            initialize();
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

	public void close() {
		try {
			lock.lock();
            Threads.removeScheduledTask(autoClearExpiredFileMonitor);
            dataMgr.close();
            indexMgr.close();
            meta.close();
		} finally {
			lock.unlock();
            removeCloseOnJvmShutdown();
		}
	}

    public void delete(){
        try {
            lock.lock();
            Threads.removeScheduledTask(autoClearExpiredFileMonitor);
            meta.clear();
            deleteAllIndexFiles();
            deleteAllDataFiles();
            meta.closeAndDeleteFile();
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            lock.unlock();
        }
    }

    private void deleteExpiredDataFiles(){
        int store = meta.getReadDataIndex();
        for(int i=indexOfLastDeleteStoreFile;i<store;i++){
            dataMgr.deleteDataFile(i);
            indexOfLastDeleteStoreFile = i;
        }
    }

    private void deleteExpiredIndexFiles(){
        int index = meta.getReadIndex();
        for(int i=indexOfLastDeleteIndexFile;i<index;i++){
            indexMgr.deleteIndexFile(i);
            indexOfLastDeleteIndexFile = i;
        }
    }

    private void deleteAllDataFiles(){
        int store = meta.getWriteDataIndex();
        for(int i=indexOfLastDeleteStoreFile;i<=store;i++){
            dataMgr.deleteDataFile(i);
            indexOfLastDeleteStoreFile = i;
        }
    }

    private void deleteAllIndexFiles(){
        int index = meta.getWriteIndex();
        for(int i=indexOfLastDeleteIndexFile;i<=index;i++){
            indexMgr.deleteIndexFile(i);
            indexOfLastDeleteIndexFile = i;
        }
    }

    void addCloseOnJvmShutdown(){
        if(!closeOnJvmShutdown){
            closeOnJvmShutdown = true;
            CloseOnJvmShutdownHook.getInstance().addJvmShutdownHook(this);
        }
    }

    void removeCloseOnJvmShutdown(){
        if(closeOnJvmShutdown){
            closeOnJvmShutdown = false;
            CloseOnJvmShutdownHook.getInstance().removeJvmShutdownHook(this);
        }
    }

    Runnable autoClearExpiredFileMonitor = new Runnable() {
        @Override
        public void run() {
            try{
                lock.lock();
                deleteExpiredDataFiles();
                deleteExpiredIndexFiles();
                if(size() == 0 && (meta.getWriteIndex() > 0 || meta.getWriteDataIndex() > 0)){
                    renew();
                }
            }catch (Throwable t){

            } finally {
                lock.unlock();
            }
        }
    } ;
}
