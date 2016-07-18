package com.lamfire.filequeue;

import java.io.IOException;

interface Writer {

    public void write(byte[] bytes) throws IOException;

    public void write(byte[] bytes, int offset, int length) throws IOException;
}
