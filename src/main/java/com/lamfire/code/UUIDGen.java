package com.lamfire.code;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.lamfire.utils.MACAddressUtils;

public class UUIDGen {

    private static AtomicInteger atomic = new AtomicInteger(0);
    private static long          lastMillis;
    private static int           nodeId = 0;

    private UUIDGen() {

    }

    public static String uuid() {
        return uuidByTime();
    }

    public static String uuidByTime() {
        byte[] uuidBytes = getTimeUUIDBytes();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < uuidBytes.length; ++j) {
            int b = uuidBytes[j] & 0xFF;
            if (b < 0x10) sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    public static long getUniqueTimeMillis() {
        return getTimeSafe();
    }

    public static String getTimeAndNodeID() {

        return getUniqueTimeMillis() + String.format("%05d", getNodeId());
    }

    public static int getNodeId() {
        if (nodeId == 0) {
            long mac = MACAddressUtils.getMacAddressAsLong();
            long left8mac = mac >> 24;
            long xor = mac ^ left8mac;
            nodeId = Math.abs((int) (xor % 100000));
        }

        return nodeId;
    }

    public static UUID getTimeUUID() {
        return new UUID(getTimeSafe(), getNodeMac());
    }

    public static UUID getUUID(ByteBuffer raw) {
        return new UUID(raw.getLong(raw.position()), raw.getLong(raw.position() + 8));
    }

    public static UUID getUUID(InetAddress addr) {
        return new UUID(getTimeSafe(), getNodeMac());
    }

    public static UUID getUUID(InetAddress addr, long seed) {
        long mostSigBits = getTimeSafe() * seed;
        return new UUID(mostSigBits, getNodeMac());
    }

    public static byte[] decompose(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        byte[] b = new byte[16];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (most >>> ((7 - i) * 8));
            b[8 + i] = (byte) (least >>> ((7 - i) * 8));
        }
        return b;
    }

    public static byte[] getTimeUUIDBytes() {
        return createTimeUUIDBytes(getTimeSafe());
    }

    public static byte[] getTimeUUIDBytes(long timeMillis) {
        return createTimeUUIDBytes(getTimeSafe());
    }

    private static byte[] createTimeUUIDBytes(long msb) {
        long lsb = getNodeMac();
        byte[] uuidBytes = new byte[16];

        for (int i = 0; i < 8; i++)
            uuidBytes[i] = (byte) (msb >>> 8 * (7 - i));

        for (int i = 8; i < 16; i++)
            uuidBytes[i] = (byte) (lsb >>> 8 * (7 - i));

        return uuidBytes;
    }

    public static long getNodeMac() {
        return MACAddressUtils.getMacAddressAsLong();
    }

    public static synchronized long getTimeSafe() {
        long time = System.currentTimeMillis();
        if (time <= lastMillis) {
            time += atomic.incrementAndGet();
        } else {
            atomic.set(0);
        }
        lastMillis = time;
        return lastMillis;
    }
}
