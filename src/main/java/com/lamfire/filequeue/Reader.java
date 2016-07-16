package com.lamfire.filequeue;


import java.io.IOException;

interface Reader {

    public boolean hashMore();

    public int index();

    public int offset();

    public byte[] read() throws IOException;

    public void moveTo(int i) throws IOException;

    public void moveNext() throws IOException;

    public void commit()throws IOException;
}
