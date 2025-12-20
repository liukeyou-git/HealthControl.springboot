package com.example.web.service;

import com.example.web.dto.AiHealthAnalysisRequestDto;
import com.example.web.dto.AiHealthAnalysisResponseDto;

/**
 * 个人健康AI分析
 */
public interface AiAnalyseService {

    /**
     * 进行用户健康数据AI分析
     * 
     * @param userId 用户ID
     * @param days   分析天数（默认7天）
     * @return AI分析结果
     */
    AiHealthAnalysisResponseDto analyzeUserHealth(Integer userId, Integer days);

    /**
     * 基于请求数据进行AI分析
     * 
     * @param requestDto 分析请求数据
     * @return AI分析结果
     */
    AiHealthAnalysisResponseDto analyzeHealthData(AiHealthAnalysisRequestDto requestDto);
}
