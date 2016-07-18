package com.lamfire.filequeue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.lamfire.logger.Logger;
import com.lamfire.utils.Maps;

class DataManager {

    private static final Logger            LOGGER     = Logger.getLogger(DataManager.class);
    private MetaBuffer                     meta;
    private String                         dir;
    private String                         name;
    private int                            bufferSize = 4 * 1024 * 1024;                    // 4m

    private final Map<Integer, DataBuffer> stores     = Maps.newHashMap();

    public DataManager(MetaBuffer meta, String dir, String name) {
        this.meta = meta;
        this.dir = dir;
        this.name = name;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean expired(int index) {
        return index < meta.getReadDataIndex();
    }

    private synchronized DataBuffer getOrNewDataBuffer(int index) throws IOException {
        DataBuffer io = stores.get(index);
        if (io == null) {
            File file = DataBuffer.getDataFile(dir, name, index);
            io = new DataBuffer(file, index, bufferSize, meta.getDataFilePartitionLength());
            stores.put(index, io);
        }
        return io;
    }

    public synchronized DataBuffer getDataBuffer(int index) throws IOException {
        if (expired(index)) {
            throw new ExpiredException("[" + name + "] : The data[" + index + "] file was expired,Readed index="
                                       + meta.getReadIndex());
        }

        if (index > meta.getWriteDataIndex()) {
            throw new FileNotFoundException("[" + name + "] : The data[" + index + "] file out of size");
        }

        DataBuffer io = getOrNewDataBuffer(index);
        return io;
    }

    public void deleteDataFile(int index) {

        DataBuffer io = stores.remove(index);
        if (io != null) {
            LOGGER.info("[" + name + "] : deleting data file [" + index + "]: "
                        + DataBuffer.getDataFileName(dir, name, index));
            io.closeAndDeleteFile();
            return;
        }

        if (DataBuffer.deleteDataFile(dir, name, index)) {
            LOGGER.info("[" + name + "] : deleting data file [" + index + "]: "
                        + DataBuffer.getDataFileName(dir, name, index));
        }

    }

    public synchronized DataBuffer createNextDataFile() throws IOException {
        meta.moveToNextDataWriteIndex();
        int index = meta.getWriteDataIndex();
        File file = DataBuffer.getDataFile(dir, name, index);
        DataBuffer io = new DataBuffer(file, index, bufferSize, meta.getDataFilePartitionLength());
        stores.put(index, io);
        return io;
    }

    public synchronized void close() {
        if (stores.isEmpty()) {
            return;
        }
        for (DataBuffer io : stores.values()) {
            io.close();
        }
        stores.clear();
    }
}
