package com.example.web.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.time.LocalDate;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Data
public class DietRecordByDaySummaryDto {
    /**
     * 记录时间
     */

    private String RecordDate;

    /**
     * 热量
     */
    @JsonProperty("TotalCalories")
    private Double TotalCalories;

    /**
     * 蛋白质
     */
    @JsonProperty("TotalProtein")
    private Double TotalProtein;

    /**
     * 糖水化合物
     */
    @JsonProperty("TotalCarbohydrates")
    private Double TotalCarbohydrates;

    /**
     * 脂肪
     */
    @JsonProperty("TotalFat")
    private Double TotalFat;

    @JsonProperty("RecordCount")
    private Integer RecordCount;
}
