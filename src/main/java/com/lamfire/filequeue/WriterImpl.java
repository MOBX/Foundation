package com.lamfire.filequeue;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class WriterImpl implements Writer {

    private Lock         lock = new ReentrantLock();
    private MetaBuffer   meta;
    private IndexManager indexMgr;
    private DataManager  dataMgr;

    public WriterImpl(MetaBuffer meta, IndexManager indexMgr, DataManager dataMgr) {
        this.meta = meta;
        this.indexMgr = indexMgr;
        this.dataMgr = dataMgr;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        try {
            lock.lock();

            DataBuffer dataIO = dataMgr.getDataBuffer(meta.getWriteDataIndex());
            dataIO.setWriteOffset(meta.getWriteDataOffset());

            if (dataIO.getFreeWriteSpace() < bytes.length) {
                dataIO = dataMgr.createNextDataFile();
            }
            int writeStoreOffset = dataIO.getWriteOffset();
            dataIO.write(bytes);
            int writeStore = dataIO.getIndex();

            int writeIndex = meta.getWriteIndex();
            IndexBuffer indexIO = indexMgr.getIndexBuffer(writeIndex);
            indexIO.setWriteOffset(meta.getWriteIndexOffset());

            if (indexIO.getFreeElementSize() <= 0) {
                indexIO = indexMgr.createNextIndexFile();
            }

            Element element = new Element(writeStore, writeStoreOffset, bytes.length);
            indexIO.add(element);
            meta.setWriteIndexOffset(meta.getWriteIndexOffset() + Element.ELEMENT_LENGTH);
            meta.setWriteDataOffset(meta.getWriteDataOffset() + bytes.length);
            meta.flush();
        } finally {
            lock.unlock();
        }
    }

    public void write(byte[] bytes, int offset, int length) throws IOException {
        try {
            lock.lock();

            DataBuffer dataIO = dataMgr.getDataBuffer(meta.getWriteDataIndex());
            dataIO.setWriteOffset(meta.getWriteDataOffset());

            if (dataIO.getFreeWriteSpace() < length) {
                dataIO = dataMgr.createNextDataFile();
            }
            int writeStoreOffset = dataIO.getWriteOffset();
            dataIO.write(bytes, offset, length);
            int writeStore = dataIO.getIndex();

            int writeIndex = meta.getWriteIndex();
            IndexBuffer indexIO = indexMgr.getIndexBuffer(writeIndex);
            indexIO.setWriteOffset(meta.getWriteIndexOffset());

            if (indexIO.getFreeElementSize() <= 0) {
                indexIO = indexMgr.createNextIndexFile();
            }

            Element element = new Element(writeStore, writeStoreOffset, length);
            indexIO.add(element);
            meta.setWriteIndexOffset(meta.getWriteIndexOffset() + Element.ELEMENT_LENGTH);
            meta.setWriteDataOffset(meta.getWriteDataOffset() + length);
            meta.flush();
        } finally {
            lock.unlock();
        }
    }
}
