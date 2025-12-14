package com.kaola.order.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单号生成工具类
 *
 * @author Kaola Team
 */
public class OrderNoUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
    private static final int MAX_SEQUENCE = 9999;

    /**
     * 生成订单号
     * 格式：前缀 + 时间戳（14位） + 序列号（4位）
     */
    public static String generateOrderNo() {
        return generateNo("O");
    }

    /**
     * 生成支付单号
     */
    public static String generatePaymentNo() {
        return generateNo("P");
    }

    /**
     * 生成提现单号
     */
    public static String generateWithdrawalNo() {
        return generateNo("W");
    }

    /**
     * 生成退款单号
     */
    public static String generateRefundNo() {
        return generateNo("R");
    }

    /**
     * 生成通用单号
     */
    public static String generateNo(String prefix) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int sequence = SEQUENCE.getAndIncrement();
        if (sequence > MAX_SEQUENCE) {
            SEQUENCE.set(0);
            sequence = SEQUENCE.getAndIncrement();
        }
        return String.format("%s%s%04d", prefix, timestamp, sequence);
    }

    /**
     * 生成带用户ID的订单号
     */
    public static String generateOrderNoWithUserId(Long userId) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int sequence = SEQUENCE.getAndIncrement() % 100;
        String userIdSuffix = String.format("%04d", userId % 10000);
        return String.format("O%s%s%02d", timestamp, userIdSuffix, sequence);
    }

    /**
     * 生成唯一标识（基于时间戳和随机数）
     */
    public static String generateUniqueId() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int random = (int) (Math.random() * 900000) + 100000;
        return timestamp + random;
    }

    /**
     * 从订单号解析时间
     */
    public static LocalDateTime parseTimeFromOrderNo(String orderNo) {
        if (orderNo == null || orderNo.length() < 15) {
            return null;
        }
        try {
            String timeStr = orderNo.substring(1, 15);
            return LocalDateTime.parse(timeStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
