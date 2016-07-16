package com.lamfire.code;

import com.lamfire.utils.*;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * RSA算法
 * 
 * @author lamfire
 * 
 */
public class RSAAlgorithm {
	static final Random random = new SecureRandom();
    static final byte RSA_BLOCK_FLAG = 0x1;   //RSA 块标识
    static final int RSA_PADDING_LENGTH = 11;   //RSA 密文头长度
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
        int blockSize = this.blockSize - RSA_PADDING_LENGTH;  //RSA_PADDING 填充，要求输入：必须 比 RSA 钥模长(modulus) 短至少11个字节, 也就是　keyBits/8 – 11
        if(source.length <= blockSize){ //数据无需分段
            return encodeBlock(source,key, modulus,keyBitLength);
        }

        // 对数据分段加密
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
        if(source.length <= blockSize){ //数据无需分段
            return decodeBlock(source,key, modulus,keyBitLength);
        }
        // 对数据分段解密
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
	 * 获取私钥
	 * 
	 * @return
	 */
	public BigInteger getPrivateKey() {
		return this.privateKey;
	}

	/**
	 * 获取公钥
	 * 
	 * @return
	 */
	public BigInteger getPublicKey() {
		return this.publicKey;
	}

	/**
	 * 获取Modulus
	 * 
	 * @return
	 */
	public BigInteger getModulus() {
		return this.modulus;
	}

	/**
	 * 获取私钥
	 * 
	 * @return
	 */
	public String getPrivateKeyAsBase64() {
		return Base64.encode(this.privateKey.toByteArray());
	}

	/**
	 * 获取公钥
	 * 
	 * @return
	 */
	public String getPublicKeyAsBase64() {
		return Base64.encode(this.publicKey.toByteArray());
	}

	/**
	 * 获取Modulus
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
		// 计算（p-1)*(q-1)
		BigInteger pq = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

		// 计算模数p*q
		BigInteger n = p.multiply(q);

		// 计算出d，即e的模n逆
		BigInteger d = e.modInverse(pq);

		// 公钥
		this.publicKey = e;

		// 私钥
		this.privateKey = d;

		// modulus
		this.modulus = n;
	}

	/**
	 * 产生指定长度的公钥和私钥
	 * 
	 * @param
	 */
    private void genKey(int keyBitLength) {
        setKeyBitLength(keyBitLength);

        // 产生两个(N/2 - 1)位的大素数p和q
        BigInteger p = genProbablePrime(keyBitLength / 2 -1);
        BigInteger q = genProbablePrime(keyBitLength / 2 -1);

        // 随便找一个e
        BigInteger e = genProbablePrime(keyBitLength / 2 -1);

        //生成公钥和私钥
        genKey(p, q, e);
	}


    /**
     * 随机产生指定位数的素数
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
     * 编码
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
     * 解码
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
