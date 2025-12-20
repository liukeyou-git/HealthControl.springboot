package com.example.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI用户健康分析输入DTO
 */
@Data
public class AiAnalyzeUserHealthDto {

    /**
     * 用户ID
     */
    @JsonProperty("UserId")
    private Integer UserId;

    /**
     * 分析天数（可选，默认7天）
     */
    @JsonProperty("Days")
    private Integer Days;

    /**
     * 开始时间（可选）
     */
    @JsonProperty("StartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime StartTime;

    /**
     * 结束时间（可选）
     */
    @JsonProperty("EndTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime EndTime;
}