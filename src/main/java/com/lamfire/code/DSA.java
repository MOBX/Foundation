package com.lamfire.code;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DSA {

    public static final String ALGORITHM = "DSA";
    private static final int   KEY_SIZE  = 1024;

    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = Base64.decode(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
        signature.initSign(priKey);
        signature.update(data);

        return Base64.encode(signature.sign());
    }

    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = Base64.decode(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
        signature.initVerify(pubKey);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(Base64.decode(sign));
    }

    public static KeyPair genKeyPair(String seed) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
        // 初始化随机产生器
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(seed.getBytes());
        keygen.initialize(KEY_SIZE, secureRandom);
        KeyPair keyPair = keygen.genKeyPair();
        return keyPair;
    }

    /**
     * 获取私钥
     * 
     * @param keyPair
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(KeyPair keyPair) throws Exception {
        Key key = keyPair.getPrivate();
        return Base64.encode(key.getEncoded());
    }

    /**
     * 获取公钥
     * 
     * @param keyPair
     * @return
     * @throws Exception
     */
    public static String getPublicKey(KeyPair keyPair) throws Exception {
        Key key = keyPair.getPublic();
        return Base64.encode(key.getEncoded());
    }
}
