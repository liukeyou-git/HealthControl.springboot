package com.example.web.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI健康分析请求DTO
 */
@Data
public class AiHealthAnalysisRequestDto {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 分析时间范围开始
     */
    private LocalDateTime startTime;

    /**
     * 分析时间范围结束
     */
    private LocalDateTime endTime;

    /**
     * 用户基本信息
     */
    private UserBasicInfo userBasicInfo;

    /**
     * 健康指标记录列表
     */
    private List<HealthIndicatorData> healthIndicators;

    /**
     * 饮食记录列表
     */
    private List<DietData> dietRecords;

    /**
     * 运动记录列表
     */
    private List<SportData> sportRecords;

    @Data
    public static class UserBasicInfo {
        private String name;
        private Integer age;
        private String gender;
        private Double height; // 身高(cm)
        private Double weight; // 体重(kg)
    }

    @Data
    public static class HealthIndicatorData {
        private String indicatorName;
        private String indicatorType;
        private Double value;
        private String threshold;
        private String content;
        private LocalDateTime recordTime;
        private String isAbnormity;
    }

    @Data
    public static class DietData {
        private String foodName;
        private String foodType;
        private Double calories;
        private Double protein;
        private Double carbohydrates;
        private Double fat;
        private Integer quantity;
        private String unit;
        private LocalDateTime recordTime;
    }

    @Data
    public static class SportData {
        private String sportName;
        private Integer sportCount; // 运动次数

        private String unit; // 单位
        private Double caloriesBurned; // 消耗热量
        private LocalDateTime recordTime;
    }
}