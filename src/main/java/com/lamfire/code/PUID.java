package com.lamfire.code;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.lamfire.logger.Logger;
import com.lamfire.utils.MACAddressUtils;
import com.lamfire.utils.RandomUtils;

public class PUID implements Comparable<PUID>, Serializable {

    private static final long          serialVersionUID = 8815279469780082174L;
    private static final Logger        LOGGER           = Logger.getLogger(PUID.class);
    private static final int           MACHINE_PROCESS_UNIQUE;
    private static final AtomicInteger INC              = new AtomicInteger(RandomUtils.nextInt());

    static {
        int macPiece = RandomUtils.nextInt();
        int processPiece = RandomUtils.nextInt();
        try {
            List<String> addresses = MACAddressUtils.getHardwareAddresses();
            StringBuffer buffer = new StringBuffer();
            for (String addr : addresses) {
                LOGGER.info("Found Hardware Address : " + addr);
                buffer.append(addr);
                buffer.append("&");
            }
            macPiece = System.identityHashCode(buffer.toString());
        } catch (Exception e) {

        }
        try {
            String process = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            processPiece = System.identityHashCode(process);
        } catch (Throwable e) {

        }
        MACHINE_PROCESS_UNIQUE = macPiece | processPiece;
        LOGGER.info(String.format("MACHINE_PROCESS_UNIQUE : [%d | %d = %d]", macPiece, processPiece,
                                  MACHINE_PROCESS_UNIQUE));
    }

    final int                          time;
    final int                          machine;
    final int                          inc;

    PUID(Date time) {
        this(time, MACHINE_PROCESS_UNIQUE, INC.getAndIncrement());
    }

    PUID(Date time, int inc) {
        this(time, MACHINE_PROCESS_UNIQUE, inc);
    }

    PUID(Date time, int machine, int inc) {
        this.time = (int) (time.getTime() / 1000L);
        this.machine = machine;
        this.inc = inc;
    }

    PUID(String source) {
        if (!isValid(source)) {
            throw new IllegalArgumentException("invalid PUID [" + source + "]");
        }

        byte[] b = new byte[12];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
        }
        ByteBuffer bb = ByteBuffer.wrap(b);
        this.time = bb.getInt();
        this.machine = bb.getInt();
        this.inc = bb.getInt();
    }

    public PUID(byte[] b) {
        if (b.length != 12) {
            throw new IllegalArgumentException("need 12 bytes");
        }
        ByteBuffer bb = ByteBuffer.wrap(b);
        this.time = bb.getInt();
        this.machine = bb.getInt();
        this.inc = bb.getInt();
    }

    public PUID(int time, int machine, int inc) {
        this.time = time;
        this.machine = machine;
        this.inc = inc;
    }

    public PUID() {
        this.time = (int) (System.currentTimeMillis() / 1000L);
        this.machine = MACHINE_PROCESS_UNIQUE;
        this.inc = INC.getAndIncrement();
    }

    public int hashCode() {
        int x = this.time;
        x += this.machine * 111;
        x += this.inc * 17;
        return x;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        PUID other = (PUID) (o);
        if (other == null) {
            return false;
        }
        return (this.time == other.time) && (this.machine == other.machine) && (this.inc == other.inc);
    }

    public String toString() {
        byte[] b = toBytes();
        return Hex.encode(b);
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.putInt(time);
        bb.putInt(MACHINE_PROCESS_UNIQUE);
        bb.putInt(this.inc);
        return bytes;
    }

    public int compareTo(PUID id) {
        if (id == null) {
            return -1;
        }
        int x = (this.time - id.time);
        if (x != 0) {
            return x;
        }
        x = (this.machine - id.machine);
        if (x != 0) {
            return x;
        }
        return (this.inc - id.inc);
    }

    public int getMachine() {
        return this.machine;
    }

    public long getTime() {
        return this.time * 1000L;
    }

    public int getTimeSecond() {
        return this.time;
    }

    public int getInc() {
        return this.inc;
    }

    public static int getMachineId() {
        return MACHINE_PROCESS_UNIQUE;
    }

    public static PUID puid() {
        return new PUID();
    }

    public static PUID make() {
        return new PUID();
    }

    public static byte[] makeAsBytes() {
        return new PUID().toBytes();
    }

    public static String makeAsString() {
        return new PUID().toString();
    }

    public static byte[] puidAsBytes() {
        return new PUID().toBytes();
    }

    public static String puidAsString() {
        return new PUID().toString();
    }

    public static boolean isValid(String s) {
        if (s == null) {
            return false;
        }
        int len = s.length();
        if (len != 24) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ((c >= '0') && (c <= '9')) continue;
            if ((c >= 'a') && (c <= 'f')) continue;
            if ((c < 'A') || (c > 'F')) {
                return false;
            }
        }
        return true;
    }

    public static PUID valueOf(byte[] bytes) {
        return new PUID(bytes);
    }

    public static PUID valueOf(String source) {
        return new PUID(source);
    }
}
