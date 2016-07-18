package com.lamfire.utils;

import java.io.*;
import java.util.*;

public class IOUtils {

    public static final char   DIR_SEPARATOR_UNIX     = '/';
    public static final char   DIR_SEPARATOR_WINDOWS  = '\\';
    public static final char   DIR_SEPARATOR          = File.separatorChar;
    public static final String LINE_SEPARATOR_UNIX    = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String LINE_SEPARATOR;
    static final int           DEFAULT_BUFFER_SIZE    = 4096;

    public static void closeQuietly(Reader input) {
        try {
            if (input != null) input.close();
        } catch (IOException ioe) {
        }
    }

    public static StringBuffer getStackTraceAsStringBuffer(Throwable t) {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        try {
            t.printStackTrace(pw);
        } finally {
            IOUtils.closeQuietly(pw);
        }
        StringBuffer buffer = writer.getBuffer();
        return buffer;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException ioe) {
        }
    }

    public static void closeQuietly(Writer output) {
        try {
            if (output != null) output.close();
        } catch (IOException ioe) {
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) input.close();
        } catch (IOException ioe) {
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) output.close();
        } catch (IOException ioe) {
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input, String encoding) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    public static char[] toCharArray(InputStream is) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream is, String encoding) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static String toString(Reader input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static final void readFully(InputStream input, byte b[]) throws IOException {
        readFully(input, b, 0, b.length);
    }

    public static final void readFully(InputStream input, byte b[], int off, int len) throws IOException {
        if (len < 0) throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = input.read(b, off + n, len - n);
            if (count < 0) throw new EOFException();
            n += count;
        }
    }

    public static int readInt(InputStream input) throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        int ch3 = input.read();
        int ch4 = input.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public static final long readLong(InputStream input) throws IOException {
        byte readBuffer[] = new byte[8];
        readFully(input, readBuffer, 0, 8);
        return (((long) readBuffer[0] << 56) + ((long) (readBuffer[1] & 255) << 48)
                + ((long) (readBuffer[2] & 255) << 40) + ((long) (readBuffer[3] & 255) << 32)
                + ((long) (readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255) << 0));
    }

    public static final float readFloat(InputStream input) throws IOException {
        return Float.intBitsToFloat(readInt(input));
    }

    public static final double readDouble(InputStream input) throws IOException {
        return Double.longBitsToDouble(readLong(input));
    }

    public static final short readShort(InputStream input) throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        if ((ch1 | ch2) < 0) throw new EOFException();
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    public static final char readChar(InputStream input) throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        if ((ch1 | ch2) < 0) throw new EOFException();
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    public static final boolean readBoolean(InputStream input) throws IOException {
        int ch = input.read();
        if (ch < 0) throw new EOFException();
        return (ch != 0);
    }

    public static final byte readByte(InputStream input) throws IOException {
        int ch = input.read();
        if (ch < 0) throw new EOFException();
        return (byte) (ch);
    }

    public static byte[] readBytes(InputStream input, int len) throws IOException {
        byte[] bytes = new byte[len];
        readFully(input, bytes);
        return bytes;
    }

    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        if (encoding == null) {
            return readLines(input);
        }
        InputStreamReader reader = new InputStreamReader(input, encoding);
        return readLines(reader);
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static Iterator<String> readLineIterator(Reader input) throws IOException {
        return new FileLineIterator(input);
    }

    public static InputStream toInputStream(String input) {
        byte[] bytes = input.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    public static final void writeBoolean(OutputStream out, boolean v) throws IOException {
        out.write(v ? 1 : 0);

    }

    public static final void writeByte(OutputStream out, int v) throws IOException {
        out.write(v);

    }

    public static final void writeShort(OutputStream out, int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);

    }

    public static final void writeChar(OutputStream out, int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);

    }

    public static final void writeLong(OutputStream out, long v) throws IOException {
        byte writeBuffer[] = new byte[8];
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 8);
    }

    public static final void writeFloat(OutputStream out, float v) throws IOException {
        writeInt(out, Float.floatToIntBits(v));
    }

    public static final void writeDouble(OutputStream out, double v) throws IOException {
        writeLong(out, Double.doubleToLongBits(v));
    }

    public static void writeBytes(OutputStream output, byte[] data) throws IOException {
        if (data != null) output.write(data);
    }

    public static final void writeChars(OutputStream out, String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }

    }

    public static final void writeInt(OutputStream out, int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static void write(OutputStream output, byte[] data) throws IOException {
        if (data != null) output.write(data);
    }

    public static void write(Writer output, byte[] data) throws IOException {
        if (data != null) output.write(new String(data));
    }

    public static void write(Writer output, byte[] data, String encoding) throws IOException {
        if (data != null) if (encoding == null) write(output, data);
        else output.write(new String(data, encoding));
    }

    public static void write(Writer output, char[] data) throws IOException {
        if (data != null) output.write(data);
    }

    public static void write(OutputStream output, char[] data) throws IOException {
        if (data != null) output.write(new String(data).getBytes());
    }

    public static void write(OutputStream output, char[] data, String encoding) throws IOException {
        if (data != null) if (encoding == null) write(output, data);
        else output.write(new String(data).getBytes(encoding));
    }

    public static void write(Writer output, String data) throws IOException {
        if (data != null) output.write(data);
    }

    public static void write(OutputStream output, String data) throws IOException {
        if (data != null) output.write(data.getBytes());
    }

    public static void write(OutputStream output, String data, String encoding) throws IOException {
        if (data != null) if (encoding == null) write(output, data);
        else output.write(data.getBytes(encoding));
    }

    public static void write(Writer output, StringBuffer data) throws IOException {
        if (data != null) output.write(data.toString());
    }

    public static void write(OutputStream output, StringBuffer data) throws IOException {
        if (data != null) output.write(data.toString().getBytes());
    }

    public static void write(OutputStream output, StringBuffer data, String encoding) throws IOException {
        if (data != null) if (encoding == null) write(output, data);
        else output.write(data.toString().getBytes(encoding));
    }

    public static void writeLines(OutputStream output, Collection<?> lines, String lineEnding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Iterator<?> it = lines.iterator(); it.hasNext();) {
            Object line = it.next();
            if (line != null) {
                output.write(line.toString().getBytes());
            }
            output.write(lineEnding.getBytes());
        }
    }

    public static void writeLines(OutputStream output, Collection<?> lines, String lineEnding, String encoding)
                                                                                                               throws IOException {
        Iterator<?> it;
        if (encoding == null) {
            writeLines(output, lines, lineEnding);
        } else {
            if (lines == null) {
                return;
            }
            if (lineEnding == null) {
                lineEnding = LINE_SEPARATOR;
            }
            for (it = lines.iterator(); it.hasNext();) {
                Object line = it.next();
                if (line != null) {
                    output.write(line.toString().getBytes(encoding));
                }
                output.write(lineEnding.getBytes(encoding));
            }
        }
    }

    public static void writeLines(Writer writer, Collection<?> lines, String lineEnding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Iterator<?> it = lines.iterator(); it.hasNext();) {
            Object line = it.next();
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, encoding);
            copy(in, output);
        }
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void copy(Reader input, OutputStream output) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);

        out.flush();
    }

    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            OutputStreamWriter out = new OutputStreamWriter(output, encoding);
            copy(input, out);

            out.flush();
        }
    }

    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return ch2 == -1;
    }

    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return ch2 == -1;
    }

    static {
        StringWriter buf = new StringWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }
}
