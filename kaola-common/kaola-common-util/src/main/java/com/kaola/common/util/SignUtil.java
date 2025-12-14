package com.kaola.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 *
 * @author Kaola Team
 */
@Slf4j
public class SignUtil {

    /**
     * MD5签名
     */
    public static String signMD5(Map<String, String> params, String key) {
        String signStr = buildSignString(params) + "&key=" + key;
        return md5(signStr).toUpperCase();
    }

    /**
     * HMAC-SHA256签名
     */
    public static String signHMACSHA256(Map<String, String> params, String key) {
        String signStr = buildSignString(params) + "&key=" + key;
        return hmacSha256(signStr, key).toUpperCase();
    }

    /**
     * 验证MD5签名
     */
    public static boolean verifyMD5(Map<String, String> params, String key, String sign) {
        String calculatedSign = signMD5(params, key);
        return calculatedSign.equals(sign);
    }

    /**
     * 验证HMAC-SHA256签名
     */
    public static boolean verifyHMACSHA256(Map<String, String> params, String key, String sign) {
        String calculatedSign = signHMACSHA256(params, key);
        return calculatedSign.equals(sign);
    }

    /**
     * 构建签名字符串
     * 按照参数名ASCII码从小到大排序
     */
    public static String buildSignString(Map<String, String> params) {
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // 跳过空值和sign字段
            if (value != null && !value.isEmpty() && !"sign".equals(key)) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(value);
            }
        }

        return sb.toString();
    }

    /**
     * MD5加密
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception e) {
            log.error("MD5加密失败", e);
            return null;
        }
    }

    /**
     * HMAC-SHA256加密
     */
    public static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception e) {
            log.error("HMAC-SHA256加密失败", e);
            return null;
        }
    }

    /**
     * SHA256加密
     */
    public static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception e) {
            log.error("SHA256加密失败", e);
            return null;
        }
    }

    /**
     * 字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
