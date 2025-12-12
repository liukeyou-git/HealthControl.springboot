package com.example.web.tools;

import com.example.web.tools.dto.LatLng;

import java.util.List;

public class LogLatExtension {

    /**
     * 计算2点的经纬度的距离(单位m)
     */
    public static double HaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径，单位为千米
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // 将结果转换为米
    }

    /**
     * 判断经纬度在某个多边形的范围之内
     */
    public static boolean PtInPolygon(LatLng point, List<LatLng> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            LatLng p1 = APoints.get(i);
            LatLng p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.longitude == p2.longitude) // p1p2 与 y=p0.y平行
                continue;
            if (point.longitude < Math.min(p1.longitude, p2.longitude)) // 交点在p1p2延长线上
                continue;
            if (point.longitude >= Math.max(p1.longitude, p2.longitude)) // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (double) (point.longitude - p1.longitude) * (double) (p2.latitude - p1.latitude)
                    / (double) (p2.longitude - p1.longitude) + p1.latitude;
            if (x > point.latitude)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }
}
