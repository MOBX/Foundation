package com.lamfire.code;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.lamfire.utils.IOUtils;

/**
 * RSA�㷨����
 * 
 * @author lamfire
 * 
 */
public class RSA {
	/**
	 * �����㷨RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * ǩ���㷨
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * ��Կ����
     */
    private int keySize = 1024;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSA(int keySize)throws Exception {
        this.keySize = keySize;
        KeyPair keyPair = genKeyPair(keySize);
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public RSA(int keySize, byte[] privateKey, byte[] publicKey) throws Exception {
        this.keySize = keySize;
        this.privateKey = toPrivateKey(privateKey);
        this.publicKey = toPublicKey(publicKey);
    }

    public RSA(int keySize, PrivateKey privateKey,PublicKey publicKey) {
        this.keySize = keySize;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey)throws Exception {
        this.privateKey = toPrivateKey(privateKey);
    }

    public void setPrivateKey(PrivateKey privateKey){
        this.privateKey = (privateKey);
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) throws Exception{
        this.publicKey = toPublicKey(publicKey);
    }

    public void setPublicKey(PublicKey publicKey){
        this.publicKey = (publicKey);
    }


	/**
	 * ��˽Կ����Ϣ��������ǩ��
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String signatureAsBase64(byte[] data) throws Exception {
		return Base64.encode(signature(data,privateKey));
	}

    public byte[] signature(byte[] data) throws Exception {
        return (signature(data,privateKey));
    }

    /**
     * У������ǩ��
     * @param data
     * @param signWithBase64
     * @return
     * @throws Exception
     */
    public boolean verifySignatureWithBase64(byte[] data,String signWithBase64) throws Exception {
        return verifySignature(data,Base64.decode(signWithBase64),publicKey);
    }

    public boolean verifySignature(byte[] data,byte[] signBytes) throws Exception {
        return verifySignature(data,signBytes,publicKey);
    }

    /**
     * ˽Կ����
     * @param data
     * @return
     * @throws Exception
     */

    public byte[] decodeByPrivateKey(byte[] data) throws Exception {
        return decode(data, privateKey,keySize);
    }

    /**
     * ��Կ����
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decodeByPublicKey(byte[] data) throws Exception {
        return decode(data, publicKey,keySize);
    }



    /**
     * ��Կ����
     * @param data
     * @return
     * @throws Exception
     */
    public  byte[] encodeByPublicKey(byte[] data) throws Exception {
        return encode(data, publicKey,keySize);
    }

    /**
     * ˽Կ����
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] encodeByPrivateKey(byte[] data) throws Exception {
        return encode(data, privateKey,keySize);
    }

    /**
     * ������Կ��(��Կ��˽Կ)
     * @return
     * @throws Exception
     */
    public static KeyPair genKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    /**
	 * ��˽Կ����Ϣ��������ǩ��
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] signature(byte[] data, PrivateKey privateKey) throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}


	
	/**
	 * У������ǩ��
	 * @param data
	 * @param sign
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySignature(byte[] data, byte[] sign,byte[] publicKey) throws Exception {
		PublicKey publicK = toPublicKey(publicKey);
		return verifySignature(data,sign,publicK);
	}
	
	/**
	 * У������ǩ��
	 * @param data
	 * @param sign
	 * @param publicK
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySignature(byte[] data, byte[] sign,PublicKey publicK) throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(sign);
	}



	/**
	 * ���ܺ���
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decode(byte[] data, Key key,int keySize) throws Exception {
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);

        int blockSize = keySize / 8;
		// �����ݷֶν���
		int inputLen = data.length;
		int offSet = 0;
		byte[] cache;
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			while (inputLen > offSet) {
				if (inputLen - offSet > blockSize) {
					cache = cipher.doFinal(data, offSet, blockSize);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * blockSize;
			}
			byte[] decryptedData = out.toByteArray();
			return decryptedData;
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
		}

	}


	/**
	 * ���ܺ���
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encode(byte[] data, Key key,int keySize) throws Exception {
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);

        int blockSize = keySize / 8 - 11;

		// �����ݷֶμ���
		int inputLen = data.length;
		int offSet = 0;
		byte[] cache;
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			while (inputLen > offSet) {
				if (inputLen - offSet > blockSize) {
					cache = cipher.doFinal(data, offSet, blockSize);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * blockSize;
			}
			byte[] encryptedData = out.toByteArray();
			return encryptedData;
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * ��ȡ˽Կ
	 * @param keyPair
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPrivateKey(KeyPair keyPair) throws Exception {
		Key key = keyPair.getPrivate();
		return (key.getEncoded());
	}

	/**
	 * ��ȡ��Կ
	 * @param keyPair
	 * @return
	 * @throws Exception
	 */
	public static  byte[] getPublicKey(KeyPair keyPair) throws Exception {
		Key key = keyPair.getPublic();
		return (key.getEncoded());
	}
	
	/**
	 * ��ȡModulus
	 * @param keyPair
	 * @return
	 */
	public static  byte[] getModulus(KeyPair keyPair){
		RSAPrivateKey key = (RSAPrivateKey)keyPair.getPrivate();
		return (key.getModulus().toByteArray());
	}
	
	/**
	 * ��ȡModulus
	 * @param key
	 * @return
	 */
	public static  byte[] getModulus(RSAPrivateKey key){
		return (key.getModulus().toByteArray());
	}
	
	/**
	 * ��ȡModulus
	 * @param key
	 * @return
	 */
	public static  byte[] getModulus(RSAPublicKey key){
		return (key.getModulus().toByteArray());
	}
	
	/**
	 * ����˽Կ
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	public static RSAPrivateKey toPrivateKey(byte[] keyBytes)throws Exception{
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey key = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
		return key;
	}
	
	/**
	 * ���빫Կ
	 * @param keyBytes
	 * @return
	 * @throws Exception
	 */
	public static RSAPublicKey toPublicKey(byte[] keyBytes)throws Exception{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey key = (RSAPublicKey)keyFactory.generatePublic(keySpec);
		return key;
	}


}