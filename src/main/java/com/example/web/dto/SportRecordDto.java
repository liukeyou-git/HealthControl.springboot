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
 * 运动记录类
 */
@Data
public class SportRecordDto extends BaseDto
{

    
     
    /**
     * 运动
     */ 
    @JsonProperty("SportId")
    private Integer SportId;          
    
     
    /**
     * 运动单位
     */ 
    @JsonProperty("SportUnitId")
    private Integer SportUnitId;          
    
     
    /**
     * 记录人
     */ 
    @JsonProperty("RecordUserId")
    private Integer RecordUserId;          
    
     
    /**
     * 记录时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("RecordTime")
    private LocalDateTime RecordTime;             
    
     
    /**
     * 记录值
     */ 
    @JsonProperty("RecordValue")
    private Integer RecordValue;          

     @JsonProperty("SportDto") 
    private SportDto SportDto;                        
   
     @JsonProperty("SportUnitDto") 
    private SportUnitDto SportUnitDto;                        
   
     @JsonProperty("RecordUserDto") 
    private AppUserDto RecordUserDto;                        
   
 	 /**
     * 把运动记录传输模型转换成运动记录实体
     */
    public SportRecord MapToEntity() throws InvocationTargetException, IllegalAccessException {
        SportRecord SportRecord= new SportRecord();
     
         BeanUtils.copyProperties(SportRecord,this);
        
        return SportRecord;
    }

}
