package com.lamfire.filequeue;

import com.lamfire.utils.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 队列数据文件读写
 * @author lamfire
 *
 */
class DataBuffer {
	public static final String FILE_SUFFIX = ".d";
	
	public static File getDataFile(String dir, String name, int index){
		return new File(getDataFileName(dir, name, index));
	}

    public static String getDataFileName(String dir, String name, int index){
        dir = FilenameUtils.normalizeNoEndSeparator(dir);
        String fileName = (name + FILE_SUFFIX + "." + index);
        return (dir+ File.separator + fileName);
    }

    public static boolean deleteDataFile(String dir, String name, int index){
          File file = getDataFile(dir, name, index);
          if(file.exists() && file.isFile()){
              return file.getAbsoluteFile().delete();
          }
        return false;
    }

	FileBuffer buffer;
	final int index;
	
	public DataBuffer(File file, int index) throws IOException {
		this.buffer = new FileBuffer(file);
		this.index = index;
	}

	public DataBuffer(File file, int index, int bufferSize) throws IOException {
		this.buffer = new FileBuffer(file,bufferSize);
		this.index = index;
	}

    public DataBuffer(File file, int index, int bufferSize,int fileMaxLen) throws IOException {
        this.buffer = new FileBuffer(file,bufferSize,fileMaxLen);
        this.index = index;
    }

	public void setWriteOffset(int offset) {
		this.buffer.setWritePosition(offset);
	}
	
	public void setReadOffset(int offset) {
		this.buffer.setReadPosition(offset);
	}

	public synchronized void write(byte[] bytes) throws IOException {
		this.buffer.put(bytes);
	}

    public synchronized void write(byte[] bytes,int offset,int length) throws IOException {
        this.buffer.put(bytes,offset,length);
    }
	
	public synchronized void read(byte[] bytes)throws IOException{
		this.buffer.get(bytes);
	}
	
	public synchronized void read(int postion ,byte[] bytes)throws IOException{
		this.buffer.setReadPosition(postion);
		read(bytes);
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
	
	public int getFreeWriteSpace(){
		return this.buffer.getFreeWriteSpace();
	}

	public int getIndex() {
		return index;
	}
	
	
}
