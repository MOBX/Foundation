package com.lamfire.filequeue;

import com.lamfire.utils.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 队列索引文件读写
 * @author lamfire
 *
 */
class IndexBuffer {
	public static final String FILE_SUFFIX = ".i";
	public static final int ELEMENT_LENGTH = Element.ELEMENT_LENGTH;

    public static String getIndexFileName(String dir,String name,int index){
        dir = FilenameUtils.normalizeNoEndSeparator(dir);
        String fileName = (name + FILE_SUFFIX + "." + index);
        return (dir+ File.separator + fileName);
    }

	public static File getIndexFile(String dir,String name,int index){
		return new File(getIndexFileName(dir,name,index));
	}

    public static boolean deleteIndexFile(String dir,String name,int index){
        File file = getIndexFile(dir, name, index);
        if(file.exists() && file.isFile()){
            return file.getAbsoluteFile().delete();
        }
        return false;
    }

	FileBuffer buffer;
	final int index;
	
	public IndexBuffer(File file, int page) throws IOException {
		this.buffer = new FileBuffer(file);
		this.index = page;
	}
	
	public IndexBuffer(File file, int page, int bufferSize) throws IOException {
		this.buffer = new FileBuffer(file,bufferSize);
		this.index = page;
	}

    public IndexBuffer(File file, int page, int bufferSize,int fileMaxLen) throws IOException {
        this.buffer = new FileBuffer(file,bufferSize,fileMaxLen);
        this.index = page;
    }
	

	public void setWriteOffset(int offset) {
		this.buffer.setWritePosition(offset);
	}
	
	public void setReadOffset(int offset) {
		this.buffer.setReadPosition(offset);
	}

	public void add(Element element) throws IOException {
		this.buffer.put(element.asBytes());	
	}
	
	public synchronized Element take()throws IOException{
		byte[] bytes = new byte[ELEMENT_LENGTH];
		this.buffer.get(bytes);
		return Element.fromBytes(bytes);
	}

	public void close() {
		this.buffer.close();
		this.buffer = null;
	}

    public void closeAndDeleteFile(){
        if(this.buffer == null){
            return;
        }
        this.buffer.closeAndDeleteFile();
        this.buffer = null;
    }

	public int getWriteOffset() {
		return this.buffer.getWritePosition();
	}
	
	public int getReadOffset() {
		return this.buffer.getReadPosition();
	}
	
	public int getFreeElementSize(){
        int freeSpace = this.buffer.getFreeWriteSpace();
		return (freeSpace - freeSpace % ELEMENT_LENGTH ) / ELEMENT_LENGTH;
	}


    public int getUnreadElementSize(){
        int unreadSpace = (this.buffer.getFileMaxLength() - this.buffer.getReadPosition());
        return (unreadSpace - unreadSpace % ELEMENT_LENGTH ) / ELEMENT_LENGTH;
    }

	public int getIndex() {
		return index;
	}

    @Override
    public String toString() {
        return "IndexIO{" +
                "index=" + index +
                ",unreadElementSize=" + getUnreadElementSize() +
                ",freeElementSize=" + getFreeElementSize() +
                '}';
    }
}
