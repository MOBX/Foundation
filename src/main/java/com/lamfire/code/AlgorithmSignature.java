package com.lamfire.code;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class AlgorithmSignature {

	public static byte[] signSHA1WithDSA(PrivateKey privateKey,byte[] data) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException{
		Signature signature = Signature.getInstance("SHA1withDSA");
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}
	
	public static boolean verifySHA1WithDSA(PublicKey pubkey,byte[] data,byte[] signature) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException{
		Signature sign = Signature.getInstance("SHA1withDSA");
		sign.initVerify(pubkey);
		sign.update(data);
		return sign.verify(signature);
	}
	
	

	public static void main(String[] args) throws Exception {

		KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");
		keygen.initialize(512);
		KeyPair keypair = keygen.generateKeyPair();
		PublicKey pubKey = keypair.getPublic();
		PrivateKey privKey = keypair.getPrivate();
		
		
		byte[] signature = AlgorithmSignature.signSHA1WithDSA(privKey, "admin".getBytes());
		
		System.out.println(Hex.encode(signature));
		
		boolean ret = AlgorithmSignature.verifySHA1WithDSA(pubKey, "admin".getBytes(), signature);
		
		System.out.println(ret);
		
	}
}
