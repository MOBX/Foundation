package com.lamfire.filequeue;

/**
 * 一个高性能持久化的列表，不支持删除元素操作
 * 
 * @author lamfire
 * 
 */
public interface FileList {
	public boolean add(byte[] bytes);
    public boolean add(byte[] bytes,int offset,int length);
	public  boolean isEmpty();
    public  byte[] get(int index);
	public  long size();
	public void clear();
	public void close();
    public void delete();
}
