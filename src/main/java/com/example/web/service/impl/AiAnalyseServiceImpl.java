package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.dto.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.AiAnalyseService;
import com.example.web.tools.DeepSeekApiClient;
import com.example.web.tools.Extension;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI分析服务实现
 */
@Slf4j
@Service
public class AiAnalyseServiceImpl implements AiAnalyseService {

    @Autowired
    private DeepSeekApiClient deepSeekApiClient;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private HealthIndicatorRecordMapper healthIndicatorRecordMapper;

    @Autowired
    private HealthIndicatorMapper healthIndicatorMapper;

    @Autowired
    private HealthIndicatorTypeMapper healthIndicatorTypeMapper;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private FoodTypeMapper foodTypeMapper;

    @Autowired
    private FoodUnitMapper foodUnitMapper;

    @Autowired
    private SportMapper sportMapper;

    @Autowired
    private SportUnitMapper sportUnitMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AiHealthAnalysisResponseDto analyzeUserHealth(Integer userId, Integer days) {
        try {
            // 获取用户数据
            AiHealthAnalysisRequestDto requestDto = buildUserHealthData(userId, days);

            // 调用分析方法
            return analyzeHealthData(requestDto);

        } catch (Exception e) {
            log.error("分析用户健康数据失败, userId: {}", userId, e);
            return buildErrorResponse("数据获取失败: " + e.getMessage());
        }
    }

    @Override
    public AiHealthAnalysisResponseDto analyzeHealthData(AiHealthAnalysisRequestDto requestDto) {
        try {
            // 构建分析提示词
            String prompt = buildAnalysisPrompt(requestDto);

            // 调用DeepSeek API
            String aiResult = deepSeekApiClient.analyzeHealth(prompt);

            if (aiResult == null || aiResult.trim().isEmpty()) {
                return buildErrorResponse("AI分析失败，请稍后重试");
            }

            // 解析AI返回的JSON结果
            AiHealthAnalysisResponseDto.AnalysisResult analysisResult = objectMapper.readValue(aiResult,
                    AiHealthAnalysisResponseDto.AnalysisResult.class);

            // 构建响应
            AiHealthAnalysisResponseDto response = new AiHealthAnalysisResponseDto();
            response.setSuccess(true);
            response.setAnalysisResult(analysisResult);
            response.setAnalysisTime(LocalDateTime.now());

            return response;

        } catch (Exception e) {
            log.error("AI健康分析失败", e);
            return buildErrorResponse("AI分析失败: " + e.getMessage());
        }
    }

    /**
     * 构建用户健康数据
     */
    private AiHealthAnalysisRequestDto buildUserHealthData(Integer userId, Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);

        AiHealthAnalysisRequestDto requestDto = new AiHealthAnalysisRequestDto();
        requestDto.setUserId(userId);
        requestDto.setStartTime(startTime);
        requestDto.setEndTime(endTime);

        // 获取用户基本信息
        AppUser user = appUserMapper.selectById(userId);
        if (user != null) {
            AiHealthAnalysisRequestDto.UserBasicInfo userBasicInfo = new AiHealthAnalysisRequestDto.UserBasicInfo();
            userBasicInfo.setName(user.getName());

            // 计算年龄
            if (user.getBirth() != null) {
                LocalDate birthDate = user.getBirth().toLocalDate();
                LocalDate now = LocalDate.now();
                userBasicInfo.setAge(Period.between(birthDate, now).getYears());
            }
            // 先查询用户的指标

            HealthIndicator heightHealthIndicator = healthIndicatorMapper.selectOne(
                    Wrappers.<HealthIndicator>lambdaQuery()
                            .eq(HealthIndicator::getBelongUserId, userId)
                            .eq(HealthIndicator::getName, "身高"));
            if (heightHealthIndicator != null) {
                // 查询用户记录这个指标的最后1条记录
                HealthIndicatorRecord healthIndicatorRecord = healthIndicatorRecordMapper.selectOne(
                        Wrappers.<HealthIndicatorRecord>lambdaQuery()
                                .eq(HealthIndicatorRecord::getRecordUserId, userId)
                                .eq(HealthIndicatorRecord::getHealthIndicatorId, heightHealthIndicator.getId())
                                .orderByDesc(HealthIndicatorRecord::getRecordTime)
                                .last("limit 1"));
                if (healthIndicatorRecord != null) {
                    userBasicInfo.setHeight(healthIndicatorRecord.getRecordValue());
                }
            }
            // 先查询用户的指标
            HealthIndicator weightHealthIndicator = healthIndicatorMapper.selectOne(
                    Wrappers.<HealthIndicator>lambdaQuery()
                            .eq(HealthIndicator::getBelongUserId, userId)
                            .eq(HealthIndicator::getName, "体重"));
            if (weightHealthIndicator != null) {
                // 查询用户记录这个指标的最后1条记录
                HealthIndicatorRecord healthIndicatorRecord = healthIndicatorRecordMapper.selectOne(
                        Wrappers.<HealthIndicatorRecord>lambdaQuery()
                                .eq(HealthIndicatorRecord::getRecordUserId, userId)
                                .eq(HealthIndicatorRecord::getHealthIndicatorId, weightHealthIndicator.getId())
                                .orderByDesc(HealthIndicatorRecord::getRecordTime)
                                .last("limit 1"));
                if (healthIndicatorRecord != null) {
                    userBasicInfo.setWeight(healthIndicatorRecord.getRecordValue());
                }
            }

            requestDto.setUserBasicInfo(userBasicInfo);
        }

        // 获取健康指标记录
        List<HealthIndicatorRecord> healthRecords = healthIndicatorRecordMapper.selectList(
                Wrappers.<HealthIndicatorRecord>lambdaQuery()
                        .eq(HealthIndicatorRecord::getRecordUserId, userId)
                        .between(HealthIndicatorRecord::getRecordTime, startTime, endTime)
                        .orderByDesc(HealthIndicatorRecord::getRecordTime));
        if (healthRecords != null && !healthRecords.isEmpty()) {
            List<AiHealthAnalysisRequestDto.HealthIndicatorData> healthIndicators = healthRecords.stream()
                    .map(record -> {
                        AiHealthAnalysisRequestDto.HealthIndicatorData data = new AiHealthAnalysisRequestDto.HealthIndicatorData();
                        HealthIndicator healthIndicator = healthIndicatorMapper
                                .selectById(record.getHealthIndicatorId());
                        data.setIndicatorName(healthIndicator.getName());
                        HealthIndicatorType healthIndicatorType = healthIndicatorTypeMapper
                                .selectById(healthIndicator.getHealthIndicatorTypeId());
                        data.setIndicatorType(healthIndicatorType.getName());
                        data.setContent(healthIndicator.getContent());
                        data.setThreshold(healthIndicator.getThreshold());
                        data.setIsAbnormity(record.getIsAbnormity());

                        // 需要关联查询获取指标名称等信息
                        data.setValue(record.getRecordValue());
                        data.setRecordTime(record.getRecordTime());
                        data.setIsAbnormity(record.getIsAbnormity());
                        return data;
                    })
                    .collect(Collectors.toList());
            requestDto.setHealthIndicators(healthIndicators);
        }

        // 获取饮食记录
        List<DietRecord> dietRecords = dietRecordMapper.selectList(
                Wrappers.<DietRecord>lambdaQuery()
                        .eq(DietRecord::getRecordUserId, userId)
                        .between(DietRecord::getRecordTime, startTime, endTime)
                        .orderByDesc(DietRecord::getRecordTime));
        if (dietRecords != null && !dietRecords.isEmpty()) {
            List<AiHealthAnalysisRequestDto.DietData> dietData = dietRecords.stream()
                    .map(record -> {
                        AiHealthAnalysisRequestDto.DietData data = new AiHealthAnalysisRequestDto.DietData();
                        // 需要关联查询获取食物信息
                        // 查询食物类型
                        Food food = foodMapper.selectById(record.getFoodId());
                        data.setFoodName(food.getName());
                        // 查询食物类型
                        FoodType foodType = foodTypeMapper.selectById(food.getFoodTypeId());
                        data.setFoodType(foodType.getName());
                        // 查询食物单位
                        FoodUnit foodUnit = foodUnitMapper.selectById(record.getFoodUnitId());
                        data.setUnit(foodUnit.getUnitName());
                        // 查询食物热量
                        data.setCalories(Extension
                                .ToFixed4(food.getCalories() * foodUnit.getUnitValue() * record.getRecordValue()));
                        data.setProtein(Extension
                                .ToFixed4(food.getProtein() * foodUnit.getUnitValue() * record.getRecordValue()));
                        data.setCarbohydrates(Extension
                                .ToFixed4(food.getCarbohydrates() * foodUnit.getUnitValue() * record.getRecordValue()));
                        data.setFat(Extension
                                .ToFixed4(food.getFat() * foodUnit.getUnitValue() * record.getRecordValue()));
                        data.setQuantity(record.getRecordValue());
                        data.setRecordTime(record.getRecordTime());
                        return data;
                    })
                    .collect(Collectors.toList());
            requestDto.setDietRecords(dietData);
        }

        // 获取运动记录
        List<SportRecord> sportRecords = sportRecordMapper.selectList(
                Wrappers.<SportRecord>lambdaQuery()
                        .eq(SportRecord::getRecordUserId, userId)
                        .between(SportRecord::getRecordTime, startTime, endTime)
                        .orderByDesc(SportRecord::getRecordTime));
        if (sportRecords != null && !sportRecords.isEmpty()) {
            List<AiHealthAnalysisRequestDto.SportData> sportData = sportRecords.stream()
                    .map(record -> {
                        AiHealthAnalysisRequestDto.SportData data = new AiHealthAnalysisRequestDto.SportData();

                        // 查询运动类型
                        Sport sport = sportMapper.selectById(record.getSportId());
                        data.setSportName(sport.getName());

                        // 需要关联查询获取运动信息和热量
                        data.setRecordTime(record.getRecordTime());

                        data.setSportCount(record.getRecordValue());
                        SportUnit sportUnit = sportUnitMapper.selectById(record.getSportUnitId());
                        data.setUnit(sportUnit.getUnitName());

                        data.setCaloriesBurned(Extension
                                .ToFixed4(sportUnitMapper.selectById(record.getSportUnitId())
                                        .getUnitValue() * record.getRecordValue()));
                        return data;
                    })
                    .collect(Collectors.toList());
            requestDto.setSportRecords(sportData);
        }

        return requestDto;
    }

    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(AiHealthAnalysisRequestDto requestDto) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请分析以下用户的健康数据：\n\n");

        // 用户基本信息
        if (requestDto.getUserBasicInfo() != null) {
            AiHealthAnalysisRequestDto.UserBasicInfo userInfo = requestDto.getUserBasicInfo();
            prompt.append("用户基本信息：\n");
            prompt.append("姓名：").append(userInfo.getName()).append("\n");
            prompt.append("年龄：").append(userInfo.getAge()).append("岁\n");
            prompt.append("性别：").append(userInfo.getGender()).append("\n");
            prompt.append("身高：").append(userInfo.getHeight()).append("cm\n");
            prompt.append("体重：").append(userInfo.getWeight()).append("kg\n\n");
        }

        // 分析时间范围
        prompt.append("分析时间范围：").append(requestDto.getStartTime()).append(" 至 ")
                .append(requestDto.getEndTime()).append("\n\n");

        // 健康指标数据
        if (requestDto.getHealthIndicators() != null && !requestDto.getHealthIndicators().isEmpty()) {
            prompt.append("健康指标记录：\n");
            for (AiHealthAnalysisRequestDto.HealthIndicatorData indicator : requestDto.getHealthIndicators()) {
                prompt.append("- ").append(indicator.getIndicatorName())
                        .append("：").append(indicator.getValue())

                        .append("（正常范围：").append(indicator.getThreshold()).append("）")
                        .append("，记录时间：").append(indicator.getRecordTime())
                        .append("，是否异常：").append("Y".equals(indicator.getIsAbnormity()) ? "是" : "否")
                        .append("\n");
            }
            prompt.append("\n");
        }

        // 饮食记录数据
        if (requestDto.getDietRecords() != null && !requestDto.getDietRecords().isEmpty()) {
            prompt.append("饮食记录：\n");
            for (AiHealthAnalysisRequestDto.DietData diet : requestDto.getDietRecords()) {
                prompt.append("- ").append(diet.getFoodName())
                        .append("：").append(diet.getQuantity()).append(diet.getUnit())
                        .append("，热量：").append(diet.getCalories()).append("kcal")
                        .append("，蛋白质：").append(diet.getProtein()).append("g")
                        .append("，碳水：").append(diet.getCarbohydrates()).append("g")
                        .append("，脂肪：").append(diet.getFat()).append("g")
                        .append("，记录时间：").append(diet.getRecordTime())
                        .append("\n");
            }
            prompt.append("\n");
        }

        // 运动记录数据
        if (requestDto.getSportRecords() != null && !requestDto.getSportRecords().isEmpty()) {
            prompt.append("运动记录：\n");
            for (AiHealthAnalysisRequestDto.SportData sport : requestDto.getSportRecords()) {
                prompt.append("- ").append(sport.getSportName())
                        .append("：").append(sport.getSportCount()).append(sport.getUnit())
                        .append("，消耗热量：").append(sport.getCaloriesBurned()).append("kcal")
                        .append("，记录时间：").append(sport.getRecordTime())
                        .append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("请基于以上数据进行全面的健康分析，并按照指定的JSON格式返回分析结果。");

        return prompt.toString();
    }

    /**
     * 构建错误响应
     */
    private AiHealthAnalysisResponseDto buildErrorResponse(String errorMessage) {
        AiHealthAnalysisResponseDto response = new AiHealthAnalysisResponseDto();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setAnalysisTime(LocalDateTime.now());
        return response;
    }
}
