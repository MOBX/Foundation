/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */
package lamfire;

import java.security.KeyPair;

import com.lamfire.code.DSA;

/**
 * @author zxc Jul 18, 2015 1:16:49 PM
 */
public class DSATest {

    public static void main(String[] args) throws Exception {
        String inputStr = "abc";
        byte[] data = inputStr.getBytes();

        // 构建密钥
        KeyPair keyPair = DSA.genKeyPair("111111111111111111111111");

        // 获得密钥
        String publicKey = DSA.getPublicKey(keyPair);
        String privateKey = DSA.getPrivateKey(keyPair);

        System.err.println("公钥:\r" + publicKey);
        System.err.println("私钥:\r" + privateKey);

        // 产生签名
        String sign = DSA.sign(data, privateKey);
        System.err.println("签名:\r" + sign);
        // 验证签名
        boolean status = DSA.verify(data, publicKey, sign);
        System.err.println("状态:\r" + status);
    }
}
