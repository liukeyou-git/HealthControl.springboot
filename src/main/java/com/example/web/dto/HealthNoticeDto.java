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
 * 健康提醒类
 */
@Data
public class HealthNoticeDto extends BaseDto
{

    
     
    /**
     * 提醒人
     */ 
    @JsonProperty("PublishUserId")
    private Integer PublishUserId;          
    
     
    /**
     * 提醒内容
     */ 
    @JsonProperty("Content")
    private String Content;
    
     
    /**
     * 提醒标题
     */ 
    @JsonProperty("Title")
    private String Title;
    
     
    /**
     * 提醒次数
     */ 
    @JsonProperty("Num")
    private Integer Num;          
    
     
    /**
     * 提醒时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("RemindTime")
    private LocalDateTime RemindTime;             
    
     
    /**
     * 提醒方式
     */ 
    @JsonProperty("RemindType")
    private String RemindType;
    
     
    /**
     * 是否提醒
     */ 
    @JsonProperty("IsRemind")
    private Boolean IsRemind;          

     @JsonProperty("PublishUserDto") 
    private AppUserDto PublishUserDto;                        
   
 	 /**
     * 把健康提醒传输模型转换成健康提醒实体
     */
    public HealthNotice MapToEntity() throws InvocationTargetException, IllegalAccessException {
        HealthNotice HealthNotice= new HealthNotice();
     
         BeanUtils.copyProperties(HealthNotice,this);
        
        return HealthNotice;
    }

}
