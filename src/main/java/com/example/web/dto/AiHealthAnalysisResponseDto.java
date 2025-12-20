package com.example.web.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI健康分析响应DTO
 */
@Data
public class AiHealthAnalysisResponseDto {

    /**
     * 分析是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 分析结果
     */
    private AnalysisResult analysisResult;

    /**
     * 分析时间
     */
    private LocalDateTime analysisTime;

    @Data
    public static class AnalysisResult {

        /**
         * 整体健康评分 (0-100)
         */
        private Integer overallHealthScore;

        /**
         * 健康状态级别 (优秀/良好/一般/较差/差)
         */
        private String healthLevel;

        /**
         * 健康风险评估
         */
        private List<HealthRisk> healthRisks;

        /**
         * 营养分析
         */
        private NutritionAnalysis nutritionAnalysis;

        /**
         * 运动分析
         */
        private SportAnalysis sportAnalysis;

        /**
         * 健康指标分析
         */
        private List<IndicatorAnalysis> indicatorAnalyses;

        /**
         * 个性化建议
         */
        private List<HealthRecommendation> recommendations;

        /**
         * 总体分析摘要
         */
        private String summary;
    }

    @Data
    public static class HealthRisk {
        /**
         * 风险类型
         */
        private String riskType;

        /**
         * 风险级别 (低/中/高)
         */
        private String riskLevel;

        /**
         * 风险描述
         */
        private String description;

        /**
         * 建议措施
         */
        private String suggestions;
    }

    @Data
    public static class NutritionAnalysis {
        /**
         * 热量摄入评估
         */
        private String calorieIntakeAssessment;

        /**
         * 营养均衡评分 (0-100)
         */
        private Integer nutritionBalanceScore;

        /**
         * 蛋白质摄入评估
         */
        private String proteinAssessment;

        /**
         * 碳水化合物摄入评估
         */
        private String carbohydrateAssessment;

        /**
         * 脂肪摄入评估
         */
        private String fatAssessment;

        /**
         * 饮食建议
         */
        private List<String> dietaryRecommendations;
    }

    @Data
    public static class SportAnalysis {
        /**
         * 运动量评估
         */
        private String exerciseVolumeAssessment;

        /**
         * 运动频率评分 (0-100)
         */
        private Integer exerciseFrequencyScore;

        /**
         * 热量消耗评估
         */
        private String caloriesBurnedAssessment;

        /**
         * 运动类型多样性评估
         */
        private String exerciseVarietyAssessment;

        /**
         * 运动建议
         */
        private List<String> exerciseRecommendations;
    }

    @Data
    public static class IndicatorAnalysis {
        /**
         * 指标名称
         */
        private String indicatorName;

        /**
         * 指标类型
         */
        private String indicatorType;

        /**
         * 当前值
         */
        private Double currentValue;

        /**
         * 正常范围
         */
        private String normalRange;

        /**
         * 状态评估 (正常/偏高/偏低/异常)
         */
        private String status;

        /**
         * 趋势分析
         */
        private String trend;

        /**
         * 具体建议
         */
        private String advice;
    }

    @Data
    public static class HealthRecommendation {
        /**
         * 建议类型 (饮食/运动/生活习惯/医疗)
         */
        private String recommendationType;

        /**
         * 优先级 (高/中/低)
         */
        private String priority;

        /**
         * 建议标题
         */
        private String title;

        /**
         * 建议内容
         */
        private String content;

        /**
         * 预期效果
         */
        private String expectedEffect;
    }
}