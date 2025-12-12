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
 * 健康指标表
 */
@Data
@TableName("`HealthIndicator`")
public class HealthIndicator extends BaseEntity {

      
  	  /**
     * 指标名称
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
     * 描述内容
     */  
    @JsonProperty("Content")
     @TableField(value="Content",updateStrategy = FieldStrategy.ALWAYS)
    private String Content;
      
  	  /**
     * 封面
     */  
    @JsonProperty("Cover")
    @TableField(value="Cover",updateStrategy = FieldStrategy.ALWAYS)
    private String Cover;
      
    /**
     * 是否公用
     */  
    @JsonProperty("IsComm")
    @TableField(value="IsComm",updateStrategy = FieldStrategy.ALWAYS)
    private Boolean IsComm;          
      
  	  /**
     * 阈值
     */  
    @JsonProperty("Threshold")
    @TableField(value="Threshold",updateStrategy = FieldStrategy.ALWAYS)
    private String Threshold;
      
    /**
     * 指标归类
     */  
    @JsonProperty("HealthIndicatorTypeId")
    @TableField(value="HealthIndicatorTypeId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer HealthIndicatorTypeId;          
  
    /**
     * 把健康指标实体转换成健康指标传输模型
     */
    public HealthIndicatorDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        HealthIndicatorDto HealthIndicatorDto = new HealthIndicatorDto();
       
        BeanUtils.copyProperties(HealthIndicatorDto,this);
       
        return HealthIndicatorDto;
    }

}
