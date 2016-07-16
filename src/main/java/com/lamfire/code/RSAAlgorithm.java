package com.lamfire.code;

import com.lamfire.utils.*;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * RSA�㷨
 * 
 * @author lamfire
 * 
 */
public class RSAAlgorithm {
	static final Random random = new SecureRandom();
    static final byte RSA_BLOCK_FLAG = 0x1;   //RSA ���ʶ
    static final int RSA_PADDING_LENGTH = 11;   //RSA ����ͷ����
    private int keyBitLength = 1024;
	private BigInteger publicKey;
	private BigInteger privateKey;
	private BigInteger modulus;
    private int blockSize;

	public RSAAlgorithm(int keyBitLength) {
        setKeyBitLength(keyBitLength);
	}

	public RSAAlgorithm(int keyBitLength,BigInteger p, BigInteger q, BigInteger e) {
        setKeyBitLength(keyBitLength);
		genKey(p, q, e);
	}


    private void assertBlock(byte[] bytes){
         Asserts.equalsAssert(bytes.length, keyBitLength / 8);
    }


    public byte[] encode(byte[] source,BigInteger key,BigInteger modulus){
        int blockSize = this.blockSize - RSA_PADDING_LENGTH;  //RSA_PADDING ��䣬Ҫ�����룺���� �� RSA Կģ��(modulus) ������11���ֽ�, Ҳ���ǡ�keyBits/8 �C 11
        if(source.length <= blockSize){ //��������ֶ�
            return encodeBlock(source,key, modulus,keyBitLength);
        }

        // �����ݷֶμ���
        int inputLen = source.length;
        int offSet = 0;
        byte[] cache;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while (inputLen > offSet) {
                if (inputLen - offSet > blockSize) {
                    cache = encodeBlock(source, offSet, blockSize, key, modulus,keyBitLength);
                } else {
                    cache = encodeBlock(source, offSet, inputLen - offSet, key, modulus,keyBitLength);
                }
                assertBlock(cache);
                out.write(cache, 0, cache.length);
                offSet += blockSize;
            }
            byte[] encryptedData = out.toByteArray();
            return encryptedData;
         } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public byte[] decode(byte[] source,BigInteger key,BigInteger modulus){
        int blockSize = this.blockSize;
        if(source.length <= blockSize){ //��������ֶ�
            return decodeBlock(source,key, modulus,keyBitLength);
        }
        // �����ݷֶν���
        int inputLen = source.length;
        int offSet = 0;
        byte[] cache;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while (inputLen > offSet) {
                if (inputLen - offSet > blockSize) {
                    cache = decodeBlock(source,offSet,blockSize,key,modulus,keyBitLength);
                } else {
                    cache = decodeBlock(source,offSet,inputLen - offSet,key,modulus,keyBitLength);
                }
                out.write(cache, 0, cache.length);
                offSet += blockSize;
            }
            byte[] decryptedData = out.toByteArray();
            return decryptedData;
        }finally {
            IOUtils.closeQuietly(out);
        }
    }

    public void setKeyBitLength(int keyBitLength){
        this.keyBitLength = keyBitLength;
        this.blockSize = keyBitLength / 8;
    }

	/**
	 * ��ȡ˽Կ
	 * 
	 * @return
	 */
	public BigInteger getPrivateKey() {
		return this.privateKey;
	}

	/**
	 * ��ȡ��Կ
	 * 
	 * @return
	 */
	public BigInteger getPublicKey() {
		return this.publicKey;
	}

	/**
	 * ��ȡModulus
	 * 
	 * @return
	 */
	public BigInteger getModulus() {
		return this.modulus;
	}

	/**
	 * ��ȡ˽Կ
	 * 
	 * @return
	 */
	public String getPrivateKeyAsBase64() {
		return Base64.encode(this.privateKey.toByteArray());
	}

	/**
	 * ��ȡ��Կ
	 * 
	 * @return
	 */
	public String getPublicKeyAsBase64() {
		return Base64.encode(this.publicKey.toByteArray());
	}

	/**
	 * ��ȡModulus
	 * 
	 * @return
	 */
	public String getModulusAsBase64() {
		return Base64.encode(this.modulus.toByteArray());
	}

    public void genKey(){
        genKey(keyBitLength);
    }

	private void genKey(BigInteger p, BigInteger q, BigInteger e) {
		// ���㣨p-1)*(q-1)
		BigInteger pq = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

		// ����ģ��p*q
		BigInteger n = p.multiply(q);

		// �����d����e��ģn��
		BigInteger d = e.modInverse(pq);

		// ��Կ
		this.publicKey = e;

		// ˽Կ
		this.privateKey = d;

		// modulus
		this.modulus = n;
	}

	/**
	 * ����ָ�����ȵĹ�Կ��˽Կ
	 * 
	 * @param
	 */
    private void genKey(int keyBitLength) {
        setKeyBitLength(keyBitLength);

        // ��������(N/2 - 1)λ�Ĵ�����p��q
        BigInteger p = genProbablePrime(keyBitLength / 2 -1);
        BigInteger q = genProbablePrime(keyBitLength / 2 -1);

        // �����һ��e
        BigInteger e = genProbablePrime(keyBitLength / 2 -1);

        //���ɹ�Կ��˽Կ
        genKey(p, q, e);
	}


    /**
     * �������ָ��λ��������
     * @param bitLength
     * @return
     */
    public static BigInteger genProbablePrime(int bitLength){
        return BigInteger.probablePrime(bitLength, random);
    }


    private static byte[] paddingBlock(final byte[] bytes,int blockSize){
        if(bytes.length > (blockSize - RSA_BLOCK_FLAG)){
            throw new RuntimeException("Message too large");
        }
        byte[] padding = new byte[blockSize];
        padding[0] = RSA_BLOCK_FLAG;
        int crc32 = CRC32.digest(bytes);
        Bytes.putInt(padding,1,crc32);
        int len = bytes.length;
        Bytes.putInt(padding,5,len);
        Bytes.putBytes(padding,blockSize - len,bytes,0,len);
        return padding;
    }

    private static byte[] recoveryPaddingBlock(final byte[] bytes,int blockSize){
        if(bytes [0] != RSA_BLOCK_FLAG){
            throw new RuntimeException("Not RSA Block");
        }
        int crc32 = Bytes.toInt(bytes,1);
        int len = Bytes.toInt(bytes,5);
        byte[] data = Bytes.subBytes(bytes,blockSize - len,len);
        int dataCrc32 = CRC32.digest(data);
        if(dataCrc32 != crc32){
            throw new RuntimeException("Block CRC32 checksum failed - [data=" + dataCrc32 +",source=" + crc32 +"]");
        }
        return data;
    }

    private static byte[] fillBlock(final byte[] bytes,int blockSize){
        byte[] result = new byte[blockSize];
        int i = blockSize - bytes.length;
        Bytes.putBytes(result,i,bytes,0,bytes.length);
        return result;
    }

    /**
     * ����
     * @param bytes
     * @param key
     * @param modulus
     * @return
     */
	protected static byte[] encodeBlock(final byte[] bytes, BigInteger key, BigInteger modulus,int keyBits) {
        int block = keyBits / 8;
        byte[] padding = paddingBlock(bytes,block);
        BigInteger message = new BigInteger(padding);
        if(message.compareTo(modulus) > 0){
              throw new RuntimeException("Max.length(byte[]) of message can be (keyBitLength/8-1),to make sure that M < N.");
        }
        BigInteger encrypt = message.modPow(key, modulus);
		byte[] resultBytes =  encrypt.toByteArray();
        if(resultBytes.length < block){ //fill block
            resultBytes = fillBlock(resultBytes,block);
        }
        return resultBytes;
	}

    protected static byte[] encodeBlock(final byte[] bytes,int startIndex,int length, BigInteger key, BigInteger modulus,int keyBits) {
        byte[] source = bytes;
        if(bytes.length != length || startIndex != 0){
            source = Bytes.subBytes(bytes,startIndex,length);
        }
        return encodeBlock(source,key,modulus,keyBits);
    }

    /**
     * ����
     * @param bytes
     * @param key
     * @param modulus
     * @return
     */
    protected static byte[] decodeBlock(byte[] bytes, BigInteger key, BigInteger modulus,int keyBits) {
        BigInteger cipherMessage = new BigInteger(bytes);
        BigInteger sourceMessage = cipherMessage.modPow(key, modulus);
		byte[] decodeBytes =  sourceMessage.toByteArray();
        byte[] resultBytes = recoveryPaddingBlock(decodeBytes,keyBits / 8);
        return resultBytes;
	}

    protected static byte[] decodeBlock(final byte[] bytes,int startIndex,int length, BigInteger key, BigInteger modulus,int keyBits) {
        byte[] source = bytes;
        if(bytes.length != length || startIndex != 0){
            source = Bytes.subBytes(bytes,startIndex,length);
        }
        return decodeBlock(source, key, modulus,keyBits);
    }

}
