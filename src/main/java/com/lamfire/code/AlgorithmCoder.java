package com.lamfire.code;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AlgorithmCoder {

    public static final class Blowfish {

        // 初始化向量
        public static final byte[] InitializationVector            = new byte[8];

        // 转换模式
        public static final String Transformation_CBC_PKCS5Padding = "Blowfish/CBC/PKCS5Padding";

        // 密钥算法名称
        public static final String AlgorithmName                   = "Blowfish";
    }

    public static final class Aes {

        // 初始化向量
        public static final byte[] InitializationVector            = new byte[16];

        // 转换模式
        public static final String Transformation_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";

        // 密钥算法名称
        public static final String AlgorithmName                   = "AES";
    }

    private String algorithmName        = null;
    private String transformation       = null;
    private byte[] initializationVector = null;

    public AlgorithmCoder(String algorithmName, String transformation) {
        this.algorithmName = algorithmName;
        this.transformation = transformation;
    }

    public AlgorithmCoder(String algorithmName, String transformation, byte[] initVector) {
        this.algorithmName = algorithmName;
        this.transformation = transformation;
        this.initializationVector = initVector;
    }

    public byte[] encode(byte[] source, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException,
                                                   InvalidKeyException, InvalidAlgorithmParameterException,
                                                   IllegalBlockSizeException, BadPaddingException {
        // 根据给定的字节数组构造一个密钥 Blowfish-与给定的密钥内容相关联的密钥算法的名称
        SecretKeySpec sksSpec = new SecretKeySpec(key, this.algorithmName);

        // 返回实现指定转换的 Cipher 对象
        Cipher cipher = Cipher.getInstance(transformation);

        if (initializationVector == null) {
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
        } else {
            // 使用 initializationVector 中的字节作为 IV 来构造一个 IvParameterSpec 对象
            AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
            // 用密钥和随机源初始化此 Cipher
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec, iv);
        }

        // 加密
        byte[] encrypted = cipher.doFinal(source);

        return encrypted;

    }

    public byte[] decode(byte[] encryptedBytes, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException,
                                                           InvalidKeyException, InvalidAlgorithmParameterException,
                                                           IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec sksSpec = new SecretKeySpec(key, this.algorithmName);
        Cipher cipher = Cipher.getInstance(transformation);

        if (initializationVector == null) {
            cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
        } else {
            AlgorithmParameterSpec iv = new IvParameterSpec(initializationVector);
            cipher.init(Cipher.DECRYPT_MODE, sksSpec, iv);
        }

        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return decrypted;
    }
}
