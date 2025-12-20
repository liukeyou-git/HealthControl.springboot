package com.example.web.controller;

import com.example.web.dto.AiAnalyzeUserHealthDto;
import com.example.web.dto.AiHealthAnalysisRequestDto;
import com.example.web.dto.AiHealthAnalysisResponseDto;

import com.example.web.service.AiAnalyseService;
import com.example.web.tools.dto.ResponseData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI健康分析控制器
 */
@RestController
@RequestMapping("/AiAnalyse")
public class AiAnalyseController {

    @Autowired()
    private AiAnalyseService aiAnalyseService;

    /**
     * 分析用户健康数据
     * 缓存1小时，根据用户ID和天数作为key
     */
    @RequestMapping(value = "/AnalyzeUserHealth", method = RequestMethod.POST)
    @SneakyThrows
    @Cacheable(value = "aiHealthAnalysis", key = "#input.userId + '_' + (#input.days != null ? #input.days : 7)")
    public AiHealthAnalysisResponseDto AnalyzeUserHealth(@RequestBody AiAnalyzeUserHealthDto input) {
        // 默认分析7天数据
        Integer days = input.getDays() != null ? input.getDays() : 7;

        return aiAnalyseService.analyzeUserHealth(input.getUserId(), days);
    }

    /**
     * 基于提供的数据进行健康分析
     * 缓存1小时，根据请求数据内容的hashCode作为key
     */
    @RequestMapping(value = "/AnalyzeHealthData", method = RequestMethod.POST)
    @SneakyThrows
    @Cacheable(value = "aiHealthAnalysis", key = "#input.hashCode()")
    public AiHealthAnalysisResponseDto AnalyzeHealthData(@RequestBody AiHealthAnalysisRequestDto input) {
        return aiAnalyseService.analyzeHealthData(input);
    }

}