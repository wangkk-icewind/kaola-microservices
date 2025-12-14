package com.kaola.common.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * 随机数工具类
 *
 * @author Kaola Team
 */
public class RandomUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUMERIC = NUMBERS + LETTERS;

    /**
     * 生成指定长度的数字验证码
     */
    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成6位数字验证码
     */
    public static String generateVerifyCode() {
        return generateCode(6);
    }

    /**
     * 生成4位数字验证码
     */
    public static String generate4DigitCode() {
        return generateCode(4);
    }

    /**
     * 生成指定长度的随机字符串（字母+数字）
     */
    public static String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字母字符串
     */
    public static String generateLetterString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成UUID（不带横线）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带横线的UUID
     */
    public static String generateUUIDWithDash() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机整数
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * 生成指定范围的随机整数
     */
    public static int nextInt(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 生成32位随机字符串（用于nonce_str）
     */
    public static String generateNonceStr() {
        return generateString(32);
    }

    /**
     * 生成随机昵称
     */
    public static String generateNickname() {
        return "用户" + generateCode(8);
    }

    /**
     * 生成随机颜色（十六进制）
     */
    public static String generateColor() {
        return String.format("#%06x", RANDOM.nextInt(0xffffff + 1));
    }
}
