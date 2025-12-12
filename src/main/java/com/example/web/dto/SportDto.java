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
 * 运动参考类
 */
@Data
public class SportDto extends BaseDto
{

    
     
    /**
     * 介绍
     */ 
    @JsonProperty("Content")
    private String Content;
    
     
    /**
     * 运动名称
     */ 
    @JsonProperty("Name")
    private String Name;
    
     
    /**
     * 封面
     */ 
    @JsonProperty("Cover")
    private String Cover;

 	 /**
     * 把运动参考传输模型转换成运动参考实体
     */
    public Sport MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Sport Sport= new Sport();
     
         BeanUtils.copyProperties(Sport,this);
        
        return Sport;
    }

}
