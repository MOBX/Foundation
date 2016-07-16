package com.lamfire.code;

/**
 * Created by lamfire on 16/3/24.
 */
public class HMAC {

    private Hash hash;

    public HMAC(Hash hash){
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
         * If key's length is longer than 64,then use hash to digest it and use
         * the result as actual key. �����Կ���ȣ�����64�ֽڣ���ʹ�ù�ϣ�㷨��������ժҪ����Ϊ��������Կ��
         */

        if (key.length > length) {

            actualKey = hash.hashDigest(key);

        }

        for (int i = 0; i < actualKey.length; i++) {

            keyArr[i] = actualKey[i];

        }

        /*
         * append zeros to K �����Կ���Ȳ���64�ֽڣ���ʹ��0x00���뵽64�ֽڡ�
         */

        if (actualKey.length < length) {
            for (int i = actualKey.length; i < keyArr.length; i++) {
                keyArr[i] = 0x00;
            }
        }

        /*
         * calc K XOR ipad ʹ����Կ��ipad����������㡣
         */

        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

        /*
         * append "text" to the end of "K XOR ipad" ������������׷�ӵ�K XOR ipad���������档
         */

        byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
        for (int i = 0; i < kIpadXorResult.length; i++) {
            firstAppendResult[i] = kIpadXorResult[i];
        }

        for (int i = 0; i < data.length; i++) {
            firstAppendResult[i + keyArr.length] = data[i];
        }



        /*
         * calc H(K XOR ipad, text) ʹ�ù�ϣ�㷨������������ժҪ��
         */

        byte[] firstHashResult = hash.hashDigest(firstAppendResult);

        /*
         * calc K XOR opad ʹ����Կ��opad����������㡣
         */

        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

        /*
         * append "H(K XOR ipad, text)" to the end of "K XOR opad" ��H(K XOR
         * ipad, text)���׷�ӵ�K XOR opad�������
         */

        byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];

        for (int i = 0; i < kOpadXorResult.length; i++) {
            secondAppendResult[i] = kOpadXorResult[i];
        }

        for (int i = 0; i < firstHashResult.length; i++) {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

        /*
         * H(K XOR opad, H(K XOR ipad, text)) ����������ݽ��й�ϣ���㡣
         */

        byte[] result = hash.hashDigest(secondAppendResult);
        return result;

    }

}
