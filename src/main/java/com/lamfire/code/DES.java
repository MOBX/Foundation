package com.lamfire.code;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.*;
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
}
