package com.lamfire.filequeue;

import java.io.IOException;

/**
 * FileList创建工具
 * User: lamfire
 * Date: 14-11-6
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public class FileListBuilder extends Builder<FileList> {
    @Override
    synchronized FileList make() throws IOException {
        FileListImpl fileList =  new FileListImpl(dataDir,name,indexBlockSize,dataBlockSize,indexFilePartitionLength,dataFilePartitionLength);
        if(closeOnJvmShutdown()){
            fileList.addCloseOnJvmShutdown();
        }
        return fileList;
    }
}
