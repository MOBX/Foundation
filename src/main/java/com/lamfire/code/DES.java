package com.lamfire.code;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.lamfire.utils.StringUtils;

public class DES {

	public static final String ALGORITHM = "DES";

	private static Key getKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}

	public static byte[] decode(byte[] data, String key) throws Exception {
		Key k = getKey(Base64.decode(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}


	public static byte[] encode(byte[] data, String key) throws Exception {
		Key k = getKey(Base64.decode(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}

	/**
	 * 生成密钥
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String genkey(String seed) throws Exception {
		SecureRandom secureRandom = null;

		if (StringUtils.isNotBlank(seed)) {
			secureRandom = new SecureRandom(Base64.decode(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(secureRandom);

		SecretKey secretKey = kg.generateKey();

		return Base64.encode(secretKey.getEncoded());
	}
	
	 public static void main(String [] args) throws Exception {  
	        String inputStr = "lin12345";  
	        String key = DES.genkey("lin12345");  
	        System.err.println("原文:\t" + inputStr);  
	  
	        System.err.println("密钥:\t" + key);  
	  
	        byte[] inputData = inputStr.getBytes();  
	        inputData = DES.encode(inputData, key);  
	  
	        System.err.println("加密后:\t" + Base64.encode(inputData));  
	  
	        byte[] outputData = DES.decode(inputData, key);  
	        String outputStr = new String(outputData);  
	  
	        System.err.println("解密后:\t" + outputStr);  
 
	        System.out.println(inputStr.equals(outputStr));
	    }  
}
