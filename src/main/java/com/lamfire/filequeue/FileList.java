package com.lamfire.filequeue;

/**
 * һ�������ܳ־û����б���֧��ɾ��Ԫ�ز���
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
