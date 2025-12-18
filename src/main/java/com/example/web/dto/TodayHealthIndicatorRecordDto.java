package com.example.web.dto;

import com.example.web.enums.*;
import com.example.web.tools.dto.BaseDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Date;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.example.web.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Data
public class TodayHealthIndicatorRecordDto {

    /**
     * 指标分类
     */
    @JsonProperty("HealthIndicatorTypeId")
    private Integer HealthIndicatorTypeId;

    /**
     * 健康指标
     */
    @JsonProperty("HealthIndicatorId")
    private Integer HealthIndicatorId;

    /**
     * 记录人
     */
    @JsonProperty("RecordUserId")
    private Integer RecordUserId;

    /**
     * 记录时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("RecordTime")
    private LocalDateTime RecordTime;

    /**
     * 当前记录值
     */
    @JsonProperty("RecordValue")
    private Double RecordValue;

    /**
     * 上一次记录值
     */
    @JsonProperty("LastRecordValue")
    private Double LastRecordValue;

    /**
     * 是否异常
     */
    @JsonProperty("IsAbnormity")
    private String IsAbnormity;

    @JsonProperty("HealthIndicatorDto")
    private HealthIndicatorDto HealthIndicatorDto;

    @JsonProperty("RecordUserDto")
    private AppUserDto RecordUserDto;

    @JsonProperty("HealthIndicatorTypeDto")
    private HealthIndicatorTypeDto HealthIndicatorTypeDto;
}
