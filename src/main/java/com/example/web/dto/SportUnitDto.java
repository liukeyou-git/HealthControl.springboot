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
 * 运动单位类
 */
@Data
public class SportUnitDto extends BaseDto
{

    
     
    /**
     * 运动
     */ 
    @JsonProperty("SportId")
    private Integer SportId;          
    
     
    /**
     * 单位名称
     */ 
    @JsonProperty("UnitName")
    private String UnitName;
    
     
    /**
     * 单位值
     */ 
    @JsonProperty("UnitValue")
    private Double UnitValue;      
    
     
    /**
     * 热量
     */ 
    @JsonProperty("Calories")
    private Double Calories;      

     @JsonProperty("SportDto") 
    private SportDto SportDto;                        
   
 	 /**
     * 把运动单位传输模型转换成运动单位实体
     */
    public SportUnit MapToEntity() throws InvocationTargetException, IllegalAccessException {
        SportUnit SportUnit= new SportUnit();
     
         BeanUtils.copyProperties(SportUnit,this);
        
        return SportUnit;
    }

}
