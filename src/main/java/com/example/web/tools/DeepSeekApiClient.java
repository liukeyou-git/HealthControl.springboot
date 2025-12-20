package com.example.web.tools;

import com.example.web.config.AiConfig;
import com.example.web.tools.dto.DeepSeekRequestDto;
import com.example.web.tools.dto.DeepSeekResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * DeepSeek API客户端
 */
@Slf4j
@Component
public class DeepSeekApiClient {

    @Autowired
    private AiConfig aiConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用DeepSeek API进行健康分析
     * 
     * @param prompt 分析提示词
     * @return AI分析结果JSON字符串
     */
    public String analyzeHealth(String prompt) {
        try {
            // 构建请求
            DeepSeekRequestDto request = buildRequest(prompt);

            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + aiConfig.getApiKey());
            headers.put("Content-Type", "application/json");

            String responseStr;

            // 检查是否启用模拟模式
            if (aiConfig.getMockMode() != null && aiConfig.getMockMode()) {
                // 模拟模式：从本地文件读取数据
                responseStr = readMockResponse();
                log.info("使用模拟模式，从airesult.txt读取数据");
            } else {
                // 真实模式：调用DeepSeek API
                responseStr = HttpUtils.Post(aiConfig.getApiUrl(), request, headers);
            }
            log.info("DeepSeek API响应: {}", responseStr);

            // 解析响应
            // 要处理可能responseStr返回了特殊字段 但是response 依旧能正常解析
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DeepSeekResponseDto response = objectMapper.readValue(responseStr, DeepSeekResponseDto.class);

            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                log.info("AI分析结果: {}", content);
                return content;
            } else {
                log.error("DeepSeek API返回的choices为空");
                return null;
            }

        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return null;
        }
    }

    /**
     * 构建DeepSeek API请求
     * 
     * @param prompt 分析提示词
     * @return 请求对象
     */
    private DeepSeekRequestDto buildRequest(String prompt) {
        DeepSeekRequestDto request = new DeepSeekRequestDto();
        request.setModel(aiConfig.getModel());
        request.setMax_tokens(aiConfig.getMaxTokens());
        request.setTemperature(aiConfig.getTemperature());

        // 设置JSON输出格式
        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        request.setResponse_format(responseFormat);

        // 构建消息列表
        List<DeepSeekRequestDto.Message> messages = new ArrayList<>();

        // 系统消息
        DeepSeekRequestDto.Message systemMessage = new DeepSeekRequestDto.Message();
        systemMessage.setRole("system");
        systemMessage.setContent(getSystemPrompt());
        messages.add(systemMessage);

        // 用户消息
        DeepSeekRequestDto.Message userMessage = new DeepSeekRequestDto.Message();
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        messages.add(userMessage);

        request.setMessages(messages);

        return request;
    }

    /**
     * 读取模拟响应数据
     * 
     * @return 模拟的API响应字符串
     */
    private String readMockResponse() {
        try {
            // 从项目根目录下的external-resources文件夹读取airesult.txt
            String filePath = "external-resources/airesult.txt";
            String content = Files.readString(Paths.get(filePath));
            log.info("成功读取模拟数据文件: {}", filePath);
            return content;
        } catch (IOException e) {
            log.error("读取模拟数据文件失败", e);
            // 返回一个基本的错误响应格式
            return "{\"choices\":[{\"message\":{\"content\":\"模拟数据读取失败\"}}]}";
        }
    }

    /**
     * 获取系统提示词
     * 
     * @return 系统提示词
     */
    private String getSystemPrompt() {
        return """
                你是一个专业的健康分析师，具有丰富的医学知识和数据分析经验。
                请根据用户提供的健康数据进行全面分析，并以JSON格式输出分析结果。

                分析要求：
                1. 基于科学的医学知识和健康标准进行评估
                2. 考虑用户的基本信息（年龄、性别、身高体重等）
                3. 分析健康指标的异常情况和趋势
                4. 评估饮食和运动习惯的合理性
                5. 提供个性化的健康建议
                6. 识别潜在的健康风险

                输出格式必须是标准的JSON，包含以下字段：
                {
                    "overallHealthScore": 整体健康评分(0-100的整数),
                    "healthLevel": "健康状态级别(优秀/良好/一般/较差/差)",
                    "healthRisks": [
                        {
                            "riskType": "风险类型",
                            "riskLevel": "风险级别(低/中/高)",
                            "description": "风险描述",
                            "suggestions": "建议措施"
                        }
                    ],
                    "nutritionAnalysis": {
                        "calorieIntakeAssessment": "热量摄入评估",
                        "nutritionBalanceScore": 营养均衡评分(0-100的整数),
                        "proteinAssessment": "蛋白质摄入评估",
                        "carbohydrateAssessment": "碳水化合物摄入评估",
                        "fatAssessment": "脂肪摄入评估",
                        "dietaryRecommendations": ["饮食建议1", "饮食建议2"]
                    },
                    "sportAnalysis": {
                        "exerciseVolumeAssessment": "运动量评估",
                        "exerciseFrequencyScore": 运动频率评分(0-100的整数),
                        "caloriesBurnedAssessment": "热量消耗评估",
                        "exerciseVarietyAssessment": "运动类型多样性评估",
                        "exerciseRecommendations": ["运动建议1", "运动建议2"]
                    },
                    "indicatorAnalyses": [
                        {
                            "indicatorName": "指标名称",
                            "indicatorType": "指标类型",
                            "currentValue": 当前值(数字),
                            "normalRange": "正常范围",
                            "status": "状态评估(正常/偏高/偏低/异常)",
                            "trend": "趋势分析",
                            "advice": "具体建议"
                        }
                    ],
                    "recommendations": [
                        {
                            "recommendationType": "建议类型(饮食/运动/生活习惯/医疗)",
                            "priority": "优先级(高/中/低)",
                            "title": "建议标题",
                            "content": "建议内容",
                            "expectedEffect": "预期效果"
                        }
                    ],
                    "summary": "总体分析摘要"
                }
                """;
    }
}