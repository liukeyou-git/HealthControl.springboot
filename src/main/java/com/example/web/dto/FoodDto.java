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
 * 食物类
 */
@Data
public class FoodDto extends BaseDto
{

    
     
    /**
     * 食物名称
     */ 
    @JsonProperty("Name")
    private String Name;
    
     
    /**
     * 封面
     */ 
    @JsonProperty("Cover")
    private String Cover;
    
     
    /**
     * 食物类型
     */ 
    @JsonProperty("FoodTypeId")
    private Integer FoodTypeId;          
    
     
    /**
     * 热量
     */ 
    @JsonProperty("Calories")
    private Double Calories;      
    
     
    /**
     * 蛋白质
     */ 
    @JsonProperty("Protein")
    private Double Protein;      
    
     
    /**
     * 糖水化合物
     */ 
    @JsonProperty("Carbohydrates")
    private Double Carbohydrates;      
    
     
    /**
     * 脂肪
     */ 
    @JsonProperty("Fat")
    private Double Fat;      

     @JsonProperty("FoodTypeDto") 
    private FoodTypeDto FoodTypeDto;                        
   
 	 /**
     * 把食物传输模型转换成食物实体
     */
    public Food MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Food Food= new Food();
     
         BeanUtils.copyProperties(Food,this);
        
        return Food;
    }

}
