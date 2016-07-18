package com.lamfire.code;

/**
 * MurmurHash算法：高运算性能，低碰撞率的hash算法. 是一个快速可靠的生成各种哈希数据的函数,支持 32位到128位的哈希值。 由Austin
 * Appleby创建于2008年，现已应用到Hadoop、libstdc++、nginx、libmemcached等开源系统。 2011年Appleby被Google雇佣，随后Google推出其变种的CityHash算法。
 */
public class MurmurHash {

    private MurmurHash() {
    }

    public static int hash32(final byte[] data) {
        return hash32(data, data.length, -1);
    }

    public static int hash32(byte[] bytes, int seed) {
        return hash32(bytes, 0, bytes.length, seed);
    }

    public static int hash32(byte[] bytes, int offset, int length) {
        return hash32(bytes, offset, length, -1);
    }

    public static long hash64(final byte[] data) {
        return hash64(data, 0, data.length, 0xe17a1465);
    }

    public static long hash64(byte[] bytes, int seed) {
        return hash64(bytes, 0, bytes.length, seed);
    }

    public static long hash64(byte[] bytes, int offset, int length) {
        return hash64(bytes, offset, length, 0xe17a1465);
    }

    public static int hash32(byte[] data, int offset, int length, int seed) {
        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ length;

        int len_4 = length >> 2;

        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[offset + i_4 + 3];
            k = k << 8;
            k = k | (data[offset + i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[offset + i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[offset + i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // avoid calculating modulo
        int len_m = len_4 << 2;
        int left = length - len_m;

        if (left != 0) {
            if (left >= 3) {
                h ^= (int) data[offset + length - 3] << 16;
            }
            if (left >= 2) {
                h ^= (int) data[offset + length - 2] << 8;
            }
            if (left >= 1) {
                h ^= (int) data[offset + length - 1];
            }

            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    public static long hash64(final byte[] data, int offset, int length, int seed) {
        final long m = 0xc6a4a7935bd1e995L;
        final int r = 47;

        long h = (seed & 0xffffffffl) ^ (length * m);

        int length8 = length / 8;

        for (int i = 0; i < length8; i++) {
            final int i8 = i * 8;
            long k = ((long) data[offset + i8 + 0] & 0xff) + (((long) data[offset + i8 + 1] & 0xff) << 8)
                     + (((long) data[offset + i8 + 2] & 0xff) << 16) + (((long) data[offset + i8 + 3] & 0xff) << 24)
                     + (((long) data[offset + i8 + 4] & 0xff) << 32) + (((long) data[offset + i8 + 5] & 0xff) << 40)
                     + (((long) data[offset + i8 + 6] & 0xff) << 48) + (((long) data[offset + i8 + 7] & 0xff) << 56);

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        switch (length % 8) {
            case 7:
                h ^= (long) (data[offset + (length & ~7) + 6] & 0xff) << 48;
            case 6:
                h ^= (long) (data[offset + (length & ~7) + 5] & 0xff) << 40;
            case 5:
                h ^= (long) (data[offset + (length & ~7) + 4] & 0xff) << 32;
            case 4:
                h ^= (long) (data[offset + (length & ~7) + 3] & 0xff) << 24;
            case 3:
                h ^= (long) (data[offset + (length & ~7) + 2] & 0xff) << 16;
            case 2:
                h ^= (long) (data[offset + (length & ~7) + 1] & 0xff) << 8;
            case 1:
                h ^= (long) (data[offset + (length & ~7)] & 0xff);
                h *= m;
        }
        ;

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        return h;
    }
}
