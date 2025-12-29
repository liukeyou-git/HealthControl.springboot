package com.example.web.service;

import java.util.HashMap;

public interface DataAnalysisService {

    /**
     * 获取所有数据分析结果
     * 包含10+种图表数据：饼图、柱状图、线图、面积图、雷达图、热力图、仪表盘、排行榜、环形图、堆叠图、漏斗图、散点图等
     */
    HashMap<String, Object> GetAllAnalysisData();
}