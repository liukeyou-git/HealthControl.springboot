package com.example.web.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.web.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
/**
 * 健康指标记录表
 */
@Data
@TableName("`HealthIndicatorRecord`")
public class HealthIndicatorRecord extends BaseEntity {

      
    /**
     * 健康指标
     */  
    @JsonProperty("HealthIndicatorId")
    @TableField(value="HealthIndicatorId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer HealthIndicatorId;          
      
    /**
     * 指标分类
     */  
    @JsonProperty("HealthIndicatorTypeId")
    @TableField(value="HealthIndicatorTypeId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer HealthIndicatorTypeId;          
      
    /**
     * 记录人
     */  
    @JsonProperty("RecordUserId")
    @TableField(value="RecordUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer RecordUserId;          
      
    /**
     * 记录时间
     */  
    @JsonProperty("RecordTime")
    @TableField(value="RecordTime",updateStrategy = FieldStrategy.ALWAYS)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime RecordTime;             
      
    /**
     * 记录值
     */  
    @JsonProperty("RecordValue")
    @TableField(value="RecordValue",updateStrategy = FieldStrategy.ALWAYS)
    private Double RecordValue;      
      
  	  /**
     * 是否异常
     */  
    @JsonProperty("IsAbnormity")
    @TableField(value="IsAbnormity",updateStrategy = FieldStrategy.ALWAYS)
    private String IsAbnormity;
  
    /**
     * 把健康指标记录实体转换成健康指标记录传输模型
     */
    public HealthIndicatorRecordDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        HealthIndicatorRecordDto HealthIndicatorRecordDto = new HealthIndicatorRecordDto();
       
        BeanUtils.copyProperties(HealthIndicatorRecordDto,this);
       
        return HealthIndicatorRecordDto;
    }

}
