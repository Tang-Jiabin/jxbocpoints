package com.zykj.pointsmall.common;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-29
 */

public class AESUtil {

    private static final int KEY_LENGTH = 128;
    private static final String SEED = "com.zykj";


    public static void main(String[] args) throws Exception {
        String str = "seven_tjb@163.com";

        //生成秘钥
        String encodedKey = genSecretKeyToString();
        System.out.println("秘钥：" + encodedKey);

        //加密
        String ciphertext = encryptToString(str, encodedKey);
        System.out.println("密文：" + ciphertext);

        //解密
        String plaintext = decryptToString(ciphertext, encodedKey);
        System.out.println("明文：" + plaintext);
    }

    /**
     * 生成秘钥
     *
     * @param keyLength 秘钥长度
     * @param seed      种子
     * @return 秘钥
     */
    public static SecretKey genSecretKey(int keyLength, byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        keyGenerator.init(keyLength);
        return keyGenerator.generateKey();
    }

    /**
     * 秘钥字符串
     *
     * @param keyLength 秘钥长度
     * @param seed      种子
     * @return 秘钥字符串
     */
    public static String genSecretKeyToString(int keyLength, String seed) throws Exception {
        SecretKey secretKey = genSecretKey(keyLength, seed.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }


    /**
     * 无参
     *
     * @return 秘钥字符串
     */
    public static String genSecretKeyToString() throws Exception {
        return genSecretKeyToString(KEY_LENGTH, SEED);
    }

    /**
     * 加密
     *
     * @param content   加密信息
     * @param secretKey 秘钥
     * @return 密文
     */
    public static byte[] encrypt(byte[] content, SecretKey secretKey) throws Exception {

        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(content);
    }

    /**
     * 解密
     *
     * @param content   密文
     * @param secretKey 秘钥
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, SecretKey secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    /**
     * 加密字符串
     *
     * @param content    加密信息
     * @param encodedKey 字符串秘钥
     * @return 密文
     */
    public static String encryptToString(String content, String encodedKey) throws Exception {
        SecretKey originalKey = getSecretKey(encodedKey);
        byte[] encrypt = encrypt(content.getBytes(StandardCharsets.UTF_8), originalKey);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 解密字符串
     *
     * @param content    密文
     * @param encodedKey 字符串秘钥
     * @return 明文
     */
    public static String decryptToString(String content, String encodedKey) throws Exception {
        SecretKey originalKey = getSecretKey(encodedKey);
        byte[] ciphertextByte = Base64.getDecoder().decode(content.replace("\r\n", ""));
        byte[] decrypt = decrypt(ciphertextByte, originalKey);
        return new String(decrypt);
    }

    /**
     * 字符串秘钥转对象
     *
     * @param encodedKey 字符串秘钥
     * @return 秘钥对象
     */
    private static SecretKey getSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey.replace("\r\n", ""));
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

}
