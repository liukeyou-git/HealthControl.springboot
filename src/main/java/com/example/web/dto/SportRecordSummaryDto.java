package com.example.web.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SportRecordSummaryDto {

    /**
     * 今日消耗卡路里
     */
    @JsonProperty("TotalCalories")
    private Double TotalCalories;

    /**
     * 本周消耗卡路里
     */
    @JsonProperty("TotalCaloriesWeek")
    private Double TotalCaloriesWeek;

    /**
     * 本周运动次数
     */
    @JsonProperty("TotalSportCountWeek")
    private Integer TotalSportCountWeek;

    /**
     * 本月消耗卡路里
     */
    @JsonProperty("TotalSportCountMonth")
    private Integer TotalSportCountMonth;

    /**
     * 本月消耗卡路里
     */
    @JsonProperty("TotalCaloriesMonth")
    private Double TotalCaloriesMonth;

}
