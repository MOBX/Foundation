package com.lamfire.filequeue;

import com.lamfire.utils.StringUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lamfire
 * Date: 14-11-6
 * Time: 上午10:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class Builder<E> {
    protected String dataDir;
    protected String name;
    protected int indexBlockSize = 4 * 1024 * 1024; //索引内存映射块大小
    protected int dataBlockSize = 4 * 1024 * 1024;  //数据内存映射块大小
    protected boolean closeOnJvmShutdown = false;
    protected int indexFilePartitionLength = 1024 * 1024 * 1024;   //索引文件分区大小
    protected int dataFilePartitionLength =1024 * 1024 * 1024;     //数据文件分区大小


    public boolean closeOnJvmShutdown(){
        return closeOnJvmShutdown;
    }

    public Builder<E> closeOnJvmShutdown(boolean closeOnJvmShutdown) {
        this.closeOnJvmShutdown = closeOnJvmShutdown;
        return this;
    }

    public String dataDir() {
        return dataDir;
    }

    public Builder<E> dataDir(String dataDir) {
        this.dataDir = dataDir;
        return this;
    }

    public String name() {
        return name;
    }

    public Builder<E> name(String name) {
        this.name = name;
        return this;
    }

    public int indexBlockSize() {
        return indexBlockSize;
    }

    public Builder<E> indexBlockSize(int indexBlockSize) {
        this.indexBlockSize = indexBlockSize;
        return this;
    }

    public int dataBlockSize() {
        return dataBlockSize;
    }

    public Builder<E> dataBlockSize(int dataBlockSize) {
        this.dataBlockSize = dataBlockSize;
        return this;
    }

    public Builder<E> indexFilePartitionLength(int indexFilePartitionLength){
        this.indexFilePartitionLength = indexFilePartitionLength;
        return this;
    }

    public Builder<E> dataFilePartitionLength(int dataFilePartitionLength){
        this.dataFilePartitionLength = dataFilePartitionLength;
        return this;
    }

    public E build()throws IOException{
        if(StringUtils.isBlank(dataDir)){
            throw new IllegalArgumentException("Argument 'dataDir' can not be empty.");
        }

        if(StringUtils.isBlank(name)){
            throw new IllegalArgumentException("Argument 'name' can not be empty.");
        }

        return make() ;
    }

    abstract E make() throws IOException;
}
