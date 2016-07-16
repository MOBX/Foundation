package com.lamfire.code;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AlgorithmCoder {
	
	public static final class Blowfish{
		// ��ʼ������
		public static final byte[] InitializationVector = new byte[8];

		// ת��ģʽ
		public static final String Transformation_CBC_PKCS5Padding = "Blowfish/CBC/PKCS5Padding";

		// ��Կ�㷨����
		public static final String AlgorithmName = "Blowfish";
	}
	
	public static final class Aes{
		// ��ʼ������
		public static final byte[] InitializationVector = new byte[16];

		// ת��ģʽ
		public static final String Transformation_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";

		// ��Կ�㷨����
		public static final String AlgorithmName = "AES";
	}
	

	private String algorithmName = null;
	private String transformation = null;
	private byte[] initializationVector = null;
	
	public AlgorithmCoder(String algorithmName, String transformation){
		this.algorithmName = algorithmName;
		this.transformation = transformation;
	}
	
	public AlgorithmCoder(String algorithmName, String transformation,byte[] initVector){
		this.algorithmName = algorithmName;
		this.transformation = transformation;
		this.initializationVector = initVector;
	}
	
	public byte[] encode(byte[] source,byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		// ���ݸ������ֽ����鹹��һ����Կ Blowfish-���������Կ�������������Կ�㷨������
		SecretKeySpec sksSpec = new SecretKeySpec(key, this.algorithmName);

		// ����ʵ��ָ��ת���� Cipher ����
		Cipher cipher = Cipher.getInstance(transformation);

		if(initializationVector == null){
			cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
		}else{
			// ʹ�� initializationVector �е��ֽ���Ϊ IV ������һ�� IvParameterSpec ����
			AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
			// ����Կ�����Դ��ʼ���� Cipher
			cipher.init(Cipher.ENCRYPT_MODE, sksSpec, iv);
		}

		// ����
		byte[] encrypted = cipher.doFinal(source);

		return encrypted;

	}

	public byte[] decode(byte[] encryptedBytes,byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec sksSpec = new SecretKeySpec(key, this.algorithmName);
		Cipher cipher = Cipher.getInstance(transformation);

		if(initializationVector == null){
			cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
		}else{
			AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
			cipher.init(Cipher.DECRYPT_MODE, sksSpec, iv);
		}

		byte[] decrypted = cipher.doFinal(encryptedBytes);

		return decrypted;
	}

}
