package com.lamfire.code;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DSA {
	public static final String ALGORITHM = "DSA";
	private static final int KEY_SIZE = 1024;

	public static String sign(byte[] data, String privateKey) throws Exception {
		// ������base64�����˽Կ
		byte[] keyBytes = Base64.decode(privateKey);

		// ����PKCS8EncodedKeySpec����
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM ָ���ļ����㷨
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		// ȡ˽Կ�׶���
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// ��˽Կ����Ϣ��������ǩ��
		Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
		signature.initSign(priKey);
		signature.update(data);

		return Base64.encode(signature.sign());
	}

	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {

		// ������base64����Ĺ�Կ
		byte[] keyBytes = Base64.decode(publicKey);

		// ����X509EncodedKeySpec����
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// ALGORITHM ָ���ļ����㷨
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		// ȡ��Կ�׶���
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
		signature.initVerify(pubKey);
		signature.update(data);

		// ��֤ǩ���Ƿ�����
		return signature.verify(Base64.decode(sign));
	}
	
	public static KeyPair genKeyPair(String seed) throws Exception {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
		// ��ʼ�����������
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(seed.getBytes());
		keygen.initialize(KEY_SIZE, secureRandom);
		KeyPair keyPair = keygen.genKeyPair();
		return keyPair;
	}

	/**
	 * ��ȡ˽Կ
	 * @param keyPair
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(KeyPair keyPair) throws Exception {
		Key key = keyPair.getPrivate();
		return Base64.encode(key.getEncoded());
	}

	/**
	 * ��ȡ��Կ
	 * @param keyPair
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(KeyPair keyPair) throws Exception {
		Key key = keyPair.getPublic();
		return Base64.encode(key.getEncoded());
	}

	
	
	
	public static void main(String[] args) throws Exception{
		String inputStr = "abc";  
        byte[] data = inputStr.getBytes();  
  
        // ������Կ  
        KeyPair keyPair = DSA.genKeyPair("111111111111111111111111");
  
        // �����Կ  
        String publicKey = DSA.getPublicKey(keyPair);  
        String privateKey = DSA.getPrivateKey(keyPair);  
  
        System.err.println("��Կ:\r" + publicKey);  
        System.err.println("˽Կ:\r" + privateKey);  
  
        // ����ǩ��  
        String sign = DSA.sign(data, privateKey);  
        System.err.println("ǩ��:\r" + sign);  
  
        // ��֤ǩ��  
        boolean status = DSA.verify(data, publicKey, sign);  
        System.err.println("״̬:\r" + status);  
        
	}
}
