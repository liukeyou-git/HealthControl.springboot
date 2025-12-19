package com.example.web.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class HealthIndicatorRecordGroupItemDto {
    /**
     * 指标名称
     */
    @JsonProperty("HealthIndicatorName")
    private String HealthIndicatorName;

    /**
     * 记录值
     */
    @JsonProperty("RecordValue")
    private Double RecordValue;
}
