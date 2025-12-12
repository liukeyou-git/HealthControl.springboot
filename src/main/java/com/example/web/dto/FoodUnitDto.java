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
 * 食物单位类
 */
@Data
public class FoodUnitDto extends BaseDto
{

    
     
    /**
     * 食物
     */ 
    @JsonProperty("FoodId")
    private Integer FoodId;          
    
     
    /**
     * 单位名称
     */ 
    @JsonProperty("UnitName")
    private String UnitName;
    
     
    /**
     * 单位值
     */ 
    @JsonProperty("UnitValue")
    private Integer UnitValue;          

     @JsonProperty("FoodDto") 
    private FoodDto FoodDto;                        
   
 	 /**
     * 把食物单位传输模型转换成食物单位实体
     */
    public FoodUnit MapToEntity() throws InvocationTargetException, IllegalAccessException {
        FoodUnit FoodUnit= new FoodUnit();
     
         BeanUtils.copyProperties(FoodUnit,this);
        
        return FoodUnit;
    }

}
