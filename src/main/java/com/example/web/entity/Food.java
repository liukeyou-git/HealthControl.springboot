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
 * 食物表
 */
@Data
@TableName("`Food`")
public class Food extends BaseEntity {

      
  	  /**
     * 食物名称
     */  
    @JsonProperty("Name")
    @TableField(value="Name",updateStrategy = FieldStrategy.ALWAYS)
    private String Name;
      
  	  /**
     * 封面
     */  
    @JsonProperty("Cover")
    @TableField(value="Cover",updateStrategy = FieldStrategy.ALWAYS)
    private String Cover;
      
    /**
     * 食物类型
     */  
    @JsonProperty("FoodTypeId")
    @TableField(value="FoodTypeId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer FoodTypeId;          
      
    /**
     * 热量
     */  
    @JsonProperty("Calories")
    @TableField(value="Calories",updateStrategy = FieldStrategy.ALWAYS)
    private Double Calories;      
      
    /**
     * 蛋白质
     */  
    @JsonProperty("Protein")
    @TableField(value="Protein",updateStrategy = FieldStrategy.ALWAYS)
    private Double Protein;      
      
    /**
     * 糖水化合物
     */  
    @JsonProperty("Carbohydrates")
    @TableField(value="Carbohydrates",updateStrategy = FieldStrategy.ALWAYS)
    private Double Carbohydrates;      
      
    /**
     * 脂肪
     */  
    @JsonProperty("Fat")
    @TableField(value="Fat",updateStrategy = FieldStrategy.ALWAYS)
    private Double Fat;      
  
    /**
     * 把食物实体转换成食物传输模型
     */
    public FoodDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        FoodDto FoodDto = new FoodDto();
       
        BeanUtils.copyProperties(FoodDto,this);
       
        return FoodDto;
    }

}
