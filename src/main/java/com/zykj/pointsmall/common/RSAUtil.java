package com.zykj.pointsmall.common;


import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author tang
 */
public class RSAUtil {

    private static final int KEY_LENGTH = 1024;

    public static void main(String[] args) throws Exception {

        String str = "seven_tjb@163.com";

        KeyPair keyPair = genKeyPair();
        String publicKeyStr = publicKeyToString(keyPair);
        String privateKeyStr = privateKeyToString(keyPair);
        System.out.println("公钥："+publicKeyStr);
        System.out.println("私钥："+privateKeyStr);

        //公钥加密
        String ciphertext = publicKeyEncryption(str, publicKeyStr);
        System.out.println("密文："+ciphertext);
        //私钥解密
        String plaintext = privateKeyDecryption(ciphertext, privateKeyStr);
        System.out.println("明文："+plaintext);

    }

    /**
     * 私钥字符串
     * @param keyPair 秘钥
     * @return 私钥字符串
     */
    public static String privateKeyToString(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 公钥字符串
     * @param keyPair 秘钥
     * @return 公钥字符串
     */
    public static String publicKeyToString(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 公钥加密
     * @param data 加密数据
     * @param publicKey 公钥字符串
     * @return 密文
     */
    public static String publicKeyEncryption(String data, String publicKey) throws Exception {
        byte[] encryptedBytes = encrypt(data.getBytes(), getPublicKey(publicKey));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 私钥解密字符串
     *
     * @param content 密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static String privateKeyDecryption(String content, String privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = decrypt(encryptedBytes, getPrivateKey(privateKey));
        return new String(decryptedBytes);
    }


    /**
     * 生成秘钥对
     *
     * @param keyLength 秘钥长度
     * @return 秘钥
     */
    public static KeyPair genKeyPair(int keyLength) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keyLength);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成秘钥对
     *
     * @return 秘钥
     */
    public static KeyPair genKeyPair() throws Exception {
        return genKeyPair(KEY_LENGTH);
    }

    /**
     * 获取公钥对象
     *
     * @param publicKey 公钥字符串
     * @return 公钥对象
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    /**
     * 将base64编码后的私钥字符串转成PrivateKey实例
     *
     * @param privateKey 私钥字符串
     * @return PrivateKey实例
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    /**
     * 公钥加密
     *
     * @param content   加密信息
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }


    /**
     * 私钥解密
     *
     * @param content    密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

}
