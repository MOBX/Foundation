package com.lamfire.filequeue;

/**
 * һ�������ܳ־û�����,֧���Ƚ��ȳ�
 * 
 * @author lamfire
 * 
 */
public interface FileQueue {
	public boolean push(byte[] bytes);
    public boolean push(byte[] bytes,int offset,int length);
    public  byte[] peek();
    public  byte[] peek(int i);
    public  byte[] pull();
	public  long size();
    public  boolean isEmpty();
    public void skip(int number);
	public void clear();
	public void close();
    public void delete();
}
