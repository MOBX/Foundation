package com.lamfire.filequeue;

import java.io.IOException;

/**
 * 特久化对列创建工具
 * User: lamfire
 * Date: 14-11-6
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */
public class FileQueueBuilder extends Builder<FileQueue> {

    int clearExpireFileIntervalSeconds = 300;

    public FileQueueBuilder enableAutoClearExpireFileIntervalSeconds(int clearExpireFileIntervalSeconds){
        this.clearExpireFileIntervalSeconds = clearExpireFileIntervalSeconds;
        return this;
    }

    @Override
    synchronized FileQueue make() throws IOException {
        FileQueueImpl fileQueue = new FileQueueImpl(dataDir,name,indexBlockSize,dataBlockSize,indexFilePartitionLength,dataFilePartitionLength) ;
        if(closeOnJvmShutdown()){
            fileQueue.addCloseOnJvmShutdown();
        }

        if(clearExpireFileIntervalSeconds > 0 ){
            fileQueue.enableClearExpireFile(clearExpireFileIntervalSeconds);
        }

        return fileQueue;
    }
}
