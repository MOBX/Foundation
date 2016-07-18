package com.lamfire.filequeue;

import java.io.IOException;

/**
 * FileList创建工具
 */
public class FileListBuilder extends Builder<FileList> {

    @Override
    synchronized FileList make() throws IOException {
        FileListImpl fileList = new FileListImpl(dataDir, name, indexBlockSize, dataBlockSize,
                                                 indexFilePartitionLength, dataFilePartitionLength);
        if (closeOnJvmShutdown()) {
            fileList.addCloseOnJvmShutdown();
        }
        return fileList;
    }
}
