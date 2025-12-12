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
 * 健康指标分类表
 */
@Data
@TableName("`HealthIndicatorType`")
public class HealthIndicatorType extends BaseEntity {

      
  	  /**
     * 分类名称
     */  
    @JsonProperty("Name")
    @TableField(value="Name",updateStrategy = FieldStrategy.ALWAYS)
    private String Name;
      
    /**
     * 所属人
     */  
    @JsonProperty("BelongUserId")
    @TableField(value="BelongUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer BelongUserId;          
      
    /**
     * 是否公用
     */  
    @JsonProperty("IsComm")
    @TableField(value="IsComm",updateStrategy = FieldStrategy.ALWAYS)
    private Boolean IsComm;          
  
    /**
     * 把健康指标分类实体转换成健康指标分类传输模型
     */
    public HealthIndicatorTypeDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        HealthIndicatorTypeDto HealthIndicatorTypeDto = new HealthIndicatorTypeDto();
       
        BeanUtils.copyProperties(HealthIndicatorTypeDto,this);
       
        return HealthIndicatorTypeDto;
    }

}
