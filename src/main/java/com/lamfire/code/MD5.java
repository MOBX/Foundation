package com.lamfire.code;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类 MD5
 */
public class MD5 implements Hash {

    private static final MD5 instance = new MD5();

    public static final MD5 getInstance() {
        return instance;
    }

    private MD5() {

    }

    public byte[] hashDigest(byte[] source) {
        return digest(source);
    }

    public static String hash(byte[] bytes) {
        byte[] encode = digest(bytes);
        return Hex.encode(encode);
    }

    public static String hash(String message) {
        return hash(message.getBytes());
    }

    public static String hash(String message, Charset charset) {
        return hash(message.getBytes(charset));
    }

    public static String hash(File file) throws IOException {
        byte[] encode = digest(file);
        return Hex.encode(encode);
    }

    public static byte[] digest(byte[] data) {
        return getMessageDigest().digest(data);
    }

    public static byte[] digest(File file) throws IOException {
        InputStream fis = null;
        MessageDigest digest = getMessageDigest();
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, numRead);
            }
            return digest.digest();
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
