package com.zykj.pointsmall.common;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Dean.li
 *
 */
@Slf4j
public class AES {

    public static String AES_KEY = "zykj@12345678#@!";

/**将二进制转换成16进制 
     * @param buf 
     * @return 
     */  
 public static String parseByte2HexStr(byte buf[]) {  
          StringBuffer sb = new StringBuffer();  
          for (int i = 0; i < buf.length; i++) {  
                  String hex = Integer.toHexString(buf[i] & 0xFF);  
                  if (hex.length() == 1) {  
                          hex = '0' + hex;  
                  }  
                  sb.append(hex.toUpperCase());  
          }  
          return sb.toString();  
     } 
 
 
 /**将16进制转换为二进制 
    * @param hexStr 
    * @return 
    */  
   public static byte[] parseHexStr2Byte(String hexStr) {  
           if (hexStr.length() < 1)  
                   return null;  
           byte[] result = new byte[hexStr.length()/2];  
           for (int i = 0;i< hexStr.length()/2; i++) {  
                   int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
                   int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
                   result[i] = (byte) (high * 16 + low);  
           }  
           return result;  
   }
    // 加密
    public static String Encrypt(String sSrc, String sKey){
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = new byte[0];
        byte[] encrypted = new byte[0];
        try {
            raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
           encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        //此处使用BASE64做转码功能，同时能起到2次加密的作用。
        return  parseByte2HexStr(encrypted);

    }
 
    // 解密
    public static String Decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
         
         
            try {
                byte[] original = cipher.doFinal(parseHexStr2Byte(sSrc));
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
 
    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey =  "zykj@12345678#@!";
        // 需要加密的字串
        String cSrc = "210742062";
        System.out.println(cSrc);
        // 加密
        String enString = AES.Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);
 
        // 解密
        String DeString = AES.Decrypt(enString, cKey);
        System.out.println("解密后的字串是：" + DeString);

    }


}