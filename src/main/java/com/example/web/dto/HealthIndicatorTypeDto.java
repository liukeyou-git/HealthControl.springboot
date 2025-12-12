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
/**
 * 健康指标分类类
 */
@Data
public class HealthIndicatorTypeDto extends BaseDto
{

    
     
    /**
     * 分类名称
     */ 
    @JsonProperty("Name")
    private String Name;
    
     
    /**
     * 所属人
     */ 
    @JsonProperty("BelongUserId")
    private Integer BelongUserId;          
    
     
    /**
     * 是否公用
     */ 
    @JsonProperty("IsComm")
    private Boolean IsComm;          

     @JsonProperty("BelongUserDto") 
    private AppUserDto BelongUserDto;                        
   
 	 /**
     * 把健康指标分类传输模型转换成健康指标分类实体
     */
    public HealthIndicatorType MapToEntity() throws InvocationTargetException, IllegalAccessException {
        HealthIndicatorType HealthIndicatorType= new HealthIndicatorType();
     
         BeanUtils.copyProperties(HealthIndicatorType,this);
        
        return HealthIndicatorType;
    }

}
