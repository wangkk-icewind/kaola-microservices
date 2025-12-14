package com.kaola.common.util;

import java.text.DecimalFormat;

/**
 * 距离计算工具类
 *
 * @author Kaola Team
 */
public class DistanceUtil {

    /**
     * 地球半径（米）
     */
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 计算两点之间的距离（米）
     * 使用Haversine公式
     *
     * @param lat1 纬度1
     * @param lng1 经度1
     * @param lat2 纬度2
     * @param lng2 经度2
     * @return 距离（米）
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);

        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) +
                        Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
        ));

        return s * EARTH_RADIUS;
    }

    /**
     * 格式化距离显示
     *
     * @param distance 距离（米）
     * @return 格式化后的距离字符串
     */
    public static String formatDistance(double distance) {
        if (distance < 1000) {
            return String.format("%.0fm", distance);
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            return df.format(distance / 1000) + "km";
        }
    }

    /**
     * 计算距离并格式化
     */
    public static String calculateAndFormat(double lat1, double lng1, double lat2, double lng2) {
        double distance = calculateDistance(lat1, lng1, lat2, lng2);
        return formatDistance(distance);
    }

    /**
     * 判断是否在指定范围内
     *
     * @param lat1   纬度1
     * @param lng1   经度1
     * @param lat2   纬度2
     * @param lng2   经度2
     * @param radius 半径（米）
     * @return 是否在范围内
     */
    public static boolean isWithinRadius(double lat1, double lng1, double lat2, double lng2, double radius) {
        return calculateDistance(lat1, lng1, lat2, lng2) <= radius;
    }

    /**
     * 根据距离获取描述
     */
    public static String getDistanceDescription(double distance) {
        if (distance < 100) {
            return "很近";
        } else if (distance < 500) {
            return "较近";
        } else if (distance < 1000) {
            return "一般";
        } else if (distance < 3000) {
            return "较远";
        } else {
            return "很远";
        }
    }
}
