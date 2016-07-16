package com.lamfire.code;

import com.lamfire.utils.MACAddressUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GUID {
    private static final AtomicInteger INC = new AtomicInteger();
    private static Random RND;
    private static long HDID;

    static {
        if (System.getProperty("java.security.egd") == null) {
            System.setProperty("java.security.egd", "file:/dev/urandom");
        }
        SecureRandom secureRand = new SecureRandom();
        RND = new Random(secureRand.nextLong());
        try {
            HDID = MACAddressUtils.getMacAddressAsLong();
        } catch (Exception e) {
            HDID = secureRand.nextLong();
            throw new AssertionError(e);
        }
    }

    public static byte[] make() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(HDID)
                .append(":")
                .append(System.nanoTime())
                .append(":")
                .append(INC.getAndIncrement())
                .append(":")
                .append(RND.nextLong());
        return MD5.digest(buffer.toString().getBytes());
    }


    public static String makeAsString() {
        return Hex.encode(make());
    }

    public static String toStandardFormat(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            result.append(String.format("%02x", v));
            if (i == 4 || i == 6 || i == 8 || i == 10) {
                result.append("-");
            }
        }
        return result.toString().toUpperCase();
    }

    public static String makeAsStandardFormat() {
        return toStandardFormat(make());
    }

    /**
     * Convert to the standard format for GUID
     * Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
     */
    public static String format(String guid) {
        String raw = guid.toUpperCase();
        StringBuilder sb = new StringBuilder();
        sb.append(raw.substring(0, 8))
                .append("-")
                .append(raw.substring(8, 12))
                .append("-")
                .append(raw.substring(12, 16))
                .append("-")
                .append(raw.substring(16, 20))
                .append("-")
                .append(raw.substring(20));
        return sb.toString();
    }
}






