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
 * 饮食记录类
 */
@Data
public class DietRecordDto extends BaseDto
{

    
     
    /**
     * 记录食物
     */ 
    @JsonProperty("FoodId")
    private Integer FoodId;          
    
     
    /**
     * 记录人
     */ 
    @JsonProperty("RecordUserId")
    private Integer RecordUserId;          
    
     
    /**
     * 食物单位
     */ 
    @JsonProperty("FoodUnitId")
    private Integer FoodUnitId;          
    
     
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

     @JsonProperty("FoodDto") 
    private FoodDto FoodDto;                        
   
     @JsonProperty("FoodUnitDto") 
    private FoodUnitDto FoodUnitDto;                        
   
     @JsonProperty("RecordUserDto") 
    private AppUserDto RecordUserDto;                        
   
 	 /**
     * 把饮食记录传输模型转换成饮食记录实体
     */
    public DietRecord MapToEntity() throws InvocationTargetException, IllegalAccessException {
        DietRecord DietRecord= new DietRecord();
     
         BeanUtils.copyProperties(DietRecord,this);
        
        return DietRecord;
    }

}
