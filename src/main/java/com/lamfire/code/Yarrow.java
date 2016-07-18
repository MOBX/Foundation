package com.lamfire.code;

import java.io.*;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.lamfire.logger.Logger;

public class Yarrow extends Random {

    private static final long                 serialVersionUID = 1074890743336683644L;
    private static final Logger               log              = Logger.getLogger(Yarrow.class.getName());

    private static String                     seedfile;

    static final int[][]                      bitTable         = { { 0, 0 }, { 1, 1 }, { 1, 3 }, { 1, 7 }, { 1, 15 },
            { 1, 31 }, { 1, 63 }, { 1, 127 }, { 1, 255 }, { 2, 511 }, { 2, 1023 }, { 2, 2047 }, { 2, 4095 },
            { 2, 8191 }, { 2, 16383 }, { 2, 32767 }, { 2, 65535 }, { 3, 131071 }, { 3, 262143 }, { 3, 524287 },
            { 3, 1048575 }, { 3, 2097151 }, { 3, 4194303 }, { 3, 8388607 }, { 3, 16777215 }, { 4, 33554431 },
            { 4, 67108863 }, { 4, 134217727 }, { 4, 268435455 }, { 4, 536870911 }, { 4, 1073741823 },
            { 4, 2147483647 }, { 4, -1 }                      };

    public byte[]                             ZERO_ARRAY       = new byte[16384];
    private Hashtable<EntropySource, Integer> entropySeen;
    private MessageDigest                     fast_pool;
    private MessageDigest                     reseed_ctx;
    private MessageDigest                     slow_pool;
    private Rijndael                          cipher_ctx;
    private byte[]                            allZeroString;
    private byte[]                            counter;

    private byte[]                            output_buffer;
    private byte[]                            tmp;
    private boolean                           fast_select;

    protected int                             digestSize;
    protected int                             fast_entropy;
    protected int                             fetch_counter;
    protected int                             output_count;
    protected int                             slow_entropy;

    public Yarrow() {
        try {
            seedfile = new File(new File(System.getProperty("java.io.tmpdir")), "prng.seed").toString();
        } catch (Throwable t) {
            seedfile = "prng.seed";
        }
        try {
            accumulator_init();
            reseed_init();
            generator_init(16);
            entropy_init(seedfile);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void acceptEntropy(EntropySource source, long data, int entropyGuess) {
        accept_entropy(data, source, Math.min(32, Math.min(estimateEntropy(source, data), entropyGuess)));
    }

    public void acceptTimerEntropy(EntropySource timer) {
        long now = System.currentTimeMillis();
        acceptEntropy(timer, now - timer.lastVal, 32);
    }

    public void makeKey(byte[] entropy, byte[] key, int offset, int len) {
        try {
            MessageDigest ctx = MessageDigest.getInstance("SHA1");
            int ic = 0;

            while (len > 0) {
                ic++;

                for (int i = 0; i < ic; i++) {
                    ctx.update((byte) 0);
                }

                ctx.update(entropy, 0, entropy.length);
                int bc;
                if (len > 20) {
                    ctx.digest(key, offset, 20);
                    bc = 20;
                } else {
                    byte[] hash = ctx.digest();
                    bc = Math.min(len, hash.length);
                    System.arraycopy(hash, 0, key, offset, bc);
                }

                offset += bc;
                len -= bc;
            }

            wipe(entropy);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate key: " + e.getMessage());
        }
    }

    public void wipe(byte[] data) {
        System.arraycopy(this.ZERO_ARRAY, 0, data, 0, data.length);
    }

    protected int next(int bits) {
        int[] parameters = bitTable[bits];
        int offset = getBytes(parameters[0]);

        int val = this.output_buffer[offset];

        if (parameters[0] == 4) val += (this.output_buffer[(offset + 1)] << 24)
                                       + (this.output_buffer[(offset + 2)] << 16)
                                       + (this.output_buffer[(offset + 3)] << 8);
        else if (parameters[0] == 3) val += (this.output_buffer[(offset + 1)] << 16)
                                            + (this.output_buffer[(offset + 2)] << 8);
        else if (parameters[0] == 2) {
            val += (this.output_buffer[(offset + 2)] << 8);
        }

        return val & parameters[1];
    }

    private void accept_entropy(long data, EntropySource source, int actualEntropy) {
        MessageDigest pool = this.fast_select ? this.fast_pool : this.slow_pool;
        pool.update((byte) (int) data);
        pool.update((byte) (int) (data >> 8));
        pool.update((byte) (int) (data >> 16));
        pool.update((byte) (int) (data >> 24));
        pool.update((byte) (int) (data >> 32));
        pool.update((byte) (int) (data >> 40));
        pool.update((byte) (int) (data >> 48));
        pool.update((byte) (int) (data >> 56));

        this.fast_select = (!this.fast_select);

        if (this.fast_select) {
            this.fast_entropy += actualEntropy;

            if (this.fast_entropy > 100) fast_pool_reseed();
        } else {
            this.slow_entropy += actualEntropy;

            if (source != null) {
                Integer contributedEntropy = (Integer) this.entropySeen.get(source);

                if (contributedEntropy == null) contributedEntropy = new Integer(actualEntropy);
                else {
                    contributedEntropy = new Integer(actualEntropy + contributedEntropy.intValue());
                }

                this.entropySeen.put(source, contributedEntropy);

                if (this.slow_entropy >= 320) {
                    int kc = 0;

                    Enumeration<EntropySource> enums = this.entropySeen.keys();
                    while (enums.hasMoreElements()) {
                        Object key = enums.nextElement();
                        Integer v = (Integer) this.entropySeen.get(key);

                        if (v.intValue() > 160) {
                            kc++;

                            if (kc >= 2) {
                                slow_pool_reseed();

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void accumulator_init() throws NoSuchAlgorithmException {
        this.fast_pool = MessageDigest.getInstance("SHA1");
        this.slow_pool = MessageDigest.getInstance("SHA1");
        this.digestSize = this.fast_pool.getDigestLength();
        this.entropySeen = new Hashtable<EntropySource, Integer>();
    }

    private final void counterInc() {
        for (int i = this.counter.length - 1; i >= 0; i--) {
            int tmp17_16 = i;
            byte[] tmp17_13 = this.counter;
            if ((tmp17_13[tmp17_16] = (byte) (tmp17_13[tmp17_16] + 1)) != 0) break;
        }
    }

    private void consumeBytes(byte[] bytes) {
        if (this.fast_select) this.fast_pool.update(bytes, 0, bytes.length);
        else {
            this.slow_pool.update(bytes, 0, bytes.length);
        }

        this.fast_select = (!this.fast_select);
    }

    private void consumeString(String str) {
        if (str == null) {
            return;
        }

        byte[] b = str.getBytes();
        consumeBytes(b);
    }

    private void entropy_init(String seed) {
        Properties sys = System.getProperties();
        EntropySource startupEntropy = new EntropySource();

        for (Enumeration<?> enums = sys.propertyNames(); enums.hasMoreElements();) {
            String key = (String) enums.nextElement();
            consumeString(key);
            consumeString(sys.getProperty(key));
        }
        try {
            consumeString(InetAddress.getLocalHost().toString());
        } catch (Exception e) {

        }
        acceptEntropy(startupEntropy, System.currentTimeMillis(), 0);
        read_seed(seed);
    }

    private final void generateOutput() {
        counterInc();
        this.cipher_ctx.encrypt(this.counter, this.output_buffer);

        if (this.output_count++ > 10) {
            this.output_count = 0;
            nextBytes(this.tmp);
            rekey(this.tmp);
        }
    }

    private synchronized int getBytes(int count) {
        if (this.fetch_counter + count > this.output_buffer.length) {
            this.fetch_counter = 0;
            generateOutput();

            return getBytes(count);
        }

        int rv = this.fetch_counter;
        this.fetch_counter += count;

        return rv;
    }

    private int estimateEntropy(EntropySource source, long newVal) {
        int delta = (int) (newVal - source.lastVal);
        int delta2 = delta - source.lastDelta;
        source.lastDelta = delta;

        int delta3 = delta2 - source.lastDelta2;
        source.lastDelta2 = delta2;

        if (delta < 0) {
            delta = -delta;
        }

        if (delta2 < 0) {
            delta2 = -delta2;
        }

        if (delta3 < 0) {
            delta3 = -delta3;
        }

        if (delta > delta2) {
            delta = delta2;
        }

        if (delta > delta3) {
            delta = delta3;
        }

        delta >>= 1;
        delta &= 4095;

        delta |= delta >> 8;
        delta |= delta >> 4;
        delta |= delta >> 2;
        delta |= delta >> 1;
        delta >>= 1;
        delta -= (delta >> 1 & 0x555);
        delta = (delta & 0x333) + (delta >> 2 & 0x333);
        delta += (delta >> 4);
        delta += (delta >> 8);

        source.lastVal = newVal;

        return delta & 0xF;
    }

    private void fast_pool_reseed() {
        byte[] v0 = this.fast_pool.digest();
        byte[] vi = v0;

        for (byte i = 0; i < 5; i = (byte) (i + 1)) {
            this.reseed_ctx.update(vi, 0, vi.length);
            this.reseed_ctx.update(v0, 0, v0.length);
            this.reseed_ctx.update(i);
            vi = this.reseed_ctx.digest();
        }

        makeKey(vi, this.tmp, 0, this.tmp.length);
        rekey(this.tmp);
        wipe(v0);
        this.fast_entropy = 0;
        write_seed(seedfile);
    }

    private void generator_init(int nBits) {
        this.cipher_ctx = new Rijndael();
        this.output_buffer = new byte[nBits];
        this.counter = new byte[nBits];
        this.allZeroString = new byte[nBits];
        this.tmp = new byte[nBits];
        this.fetch_counter = this.output_buffer.length;
    }

    private void read_seed(String filename) {
        EntropySource seedFile = new EntropySource();
        try {
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(new FileInputStream(filename));

                for (int i = 0; i < 32; i++)
                    acceptEntropy(seedFile, dis.readLong(), 64);
            } catch (Exception f) {
                try {
                    Random rand = new Random();

                    for (int i = 0; i < 32; i++)
                        acceptEntropy(seedFile, rand.nextLong(), 64);
                } catch (Exception e) {
                    log.warn("PRNG cannot do initial seed");
                }
            } finally {
                if (dis != null) dis.close();
            }
        } catch (Exception e) {
            log.warn("Could not read seed properly", e);
        }

        fast_pool_reseed();
    }

    private void rekey(byte[] key) {
        this.cipher_ctx.makeKey(key, 128);
        this.cipher_ctx.encrypt(this.allZeroString, this.counter);
        wipe(key);
    }

    private void reseed_init() throws NoSuchAlgorithmException {
        this.reseed_ctx = MessageDigest.getInstance("SHA1");
    }

    private void slow_pool_reseed() {
        byte[] slow_hash = this.slow_pool.digest();
        this.fast_pool.update(slow_hash, 0, slow_hash.length);
        fast_pool_reseed();
        this.slow_entropy = 0;

        Integer ZERO = new Integer(0);

        for (Enumeration<EntropySource> enums = this.entropySeen.keys(); enums.hasMoreElements();)
            this.entropySeen.put(enums.nextElement(), ZERO);
    }

    private void write_seed(String filename) {
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename));

            for (int i = 0; i < 32; i++) {
                dos.writeLong(nextLong());
            }

            dos.close();
        } catch (Exception e) {
            log.warn("Could not write seed");
        }
    }

    public class EntropySource {

        public int  lastDelta;
        public int  lastDelta2;
        public long lastVal;

        public EntropySource() {
        }
    }
}
