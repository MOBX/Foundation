package com.lamfire.code;

import java.io.IOException;

import com.lamfire.utils.Bytes;

/**
 * 16进制工具类 HEX
 */
public class Hex {

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f'                       };

    public static Appendable append(Appendable a, short in) {
        return append(a, in, 4);
    }

    public static Appendable append(Appendable a, short in, int length) {
        return append(a, in, length);
    }

    public static Appendable append(Appendable a, int in) {
        return append(a, in, 8);
    }

    public static Appendable append(Appendable a, int in, int length) {
        return append(a, in, length);
    }

    public static Appendable append(Appendable a, long in) {
        return append(a, in, 16);
    }

    public static Appendable append(Appendable a, long in, int length) {
        try {
            int lim = (length << 2) - 4;
            while (lim >= 0) {
                a.append(DIGITS[((byte) (int) (in >> lim) & 0xF)]);
                lim -= 4;
            }
        } catch (IOException ex) {
        }
        return a;
    }

    public static Appendable append(Appendable a, byte[] bytes) {
        try {
            for (byte b : bytes) {
                a.append(DIGITS[(byte) ((b & 0xF0) >> 4)]);
                a.append(DIGITS[(byte) (b & 0xF)]);
            }
        } catch (IOException ex) {
        }
        return a;
    }

    public static String encode(short val) {
        return encode(Bytes.toBytes(val));
    }

    public static String encode(int val) {
        return encode(Bytes.toBytes(val));
    }

    public static String encode(long val) {
        return encode(Bytes.toBytes(val));
    }

    public static String encode(byte[] data) {
        char[] charArray = encodeAsChars(data);
        return new String(charArray);
    }

    public static String encode(byte[] data, int index, int len) {
        char[] charArray = encodeAsChars(data, index, len);
        return new String(charArray);
    }

    public static char[] encodeAsChars(byte[] data) {
        int l = data.length;

        char[] out = new char[l << 1];

        int i = 0;
        for (int j = 0; i < l; i++) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }

        return out;
    }

    public static char[] encodeAsChars(byte[] data, int index, int len) {
        if (data.length < (index + len)) {
            len = data.length - index;
        }

        char[] out = new char[len << 1];

        int i = index;
        for (int j = 0; i < index + len; i++) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }

        return out;
    }

    public static byte[] decode(String hexString) {
        return decode(hexString.toCharArray());
    }

    public static byte[] decode(char[] hexChars) {
        int len = hexChars.length;

        if ((len & 0x1) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        int i = 0;
        for (int j = 0; j < len; i++) {
            int f = toDigit(hexChars[j], j) << 4;
            j++;
            f |= toDigit(hexChars[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal charcter " + ch + " at index " + index);
        }
        return digit;
    }

    public static short toShort(String hex) {
        byte[] bytes = decode(hex);
        return Bytes.toShort(bytes);
    }

    public static int toInt(String hex) {
        byte[] bytes = decode(hex);
        return Bytes.toInt(bytes);
    }

    public static long toLong(String hex) {
        byte[] bytes = decode(hex);
        return Bytes.toLong(bytes);
    }
}
