package com.lamfire.code;

public class HMAC {

    private Hash hash;

    public HMAC(Hash hash) {
        this.hash = hash;
    }

    public byte[] digest(byte[] key, byte[] data) {
        int length = 64;
        byte[] ipad = new byte[length];
        byte[] opad = new byte[length];
        for (int i = 0; i < 64; i++) {
            ipad[i] = 0x36;
            opad[i] = 0x5C;
        }

        byte[] actualKey = key; // Actual key.
        byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length

        /*
         * If key's length is longer than 64,then use hash to digest it and use the result as actual key.
         * 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
         */

        if (key.length > length) {

            actualKey = hash.hashDigest(key);

        }

        for (int i = 0; i < actualKey.length; i++) {

            keyArr[i] = actualKey[i];

        }

        /*
         * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
         */

        if (actualKey.length < length) {
            for (int i = actualKey.length; i < keyArr.length; i++) {
                keyArr[i] = 0x00;
            }
        }

        /*
         * calc K XOR ipad 使用密钥和ipad进行异或运算。
         */

        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

        /*
         * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
         */

        byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
        for (int i = 0; i < kIpadXorResult.length; i++) {
            firstAppendResult[i] = kIpadXorResult[i];
        }

        for (int i = 0; i < data.length; i++) {
            firstAppendResult[i + keyArr.length] = data[i];
        }

        /*
         * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
         */

        byte[] firstHashResult = hash.hashDigest(firstAppendResult);

        /*
         * calc K XOR opad 使用密钥和opad进行异或运算。
         */

        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

        /*
         * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR ipad, text)结果追加到K XOR opad结果后面
         */

        byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];

        for (int i = 0; i < kOpadXorResult.length; i++) {
            secondAppendResult[i] = kOpadXorResult[i];
        }

        for (int i = 0; i < firstHashResult.length; i++) {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

        /*
         * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
         */

        byte[] result = hash.hashDigest(secondAppendResult);
        return result;

    }
}
