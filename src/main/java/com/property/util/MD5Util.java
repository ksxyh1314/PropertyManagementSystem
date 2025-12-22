package com.property.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * MD5加密（32位小写）
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(plainText.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * MD5加密（32位大写）
     */
    public static String encryptUpperCase(String plainText) {
        String result = encrypt(plainText);
        return result != null ? result.toUpperCase() : null;
    }

    /**
     * 验证密码
     */
    public static boolean verify(String plainText, String md5Hash) {
        if (plainText == null || md5Hash == null) {
            return false;
        }
        String encrypted = encrypt(plainText);
        return md5Hash.equalsIgnoreCase(encrypted);
    }

    /**
     * 生成SQL Server兼容的MD5哈希
     * 格式：0x + 32位十六进制（大写）
     */
    public static String encryptForSQLServer(String plainText) {
        String hash = encryptUpperCase(plainText);
        return hash != null ? "0x" + hash : null;
    }
}
