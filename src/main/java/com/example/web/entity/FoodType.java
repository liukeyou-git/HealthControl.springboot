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
 * 食物类型表
 */
@Data
@TableName("`FoodType`")
public class FoodType extends BaseEntity {

      
  	  /**
     * 分类名称
     */  
    @JsonProperty("Name")
    @TableField(value="Name",updateStrategy = FieldStrategy.ALWAYS)
    private String Name;
      
    /**
     * 显示顺序
     */  
    @JsonProperty("Sort")
    @TableField(value="Sort",updateStrategy = FieldStrategy.ALWAYS)
    private Integer Sort;          
  
    /**
     * 把食物类型实体转换成食物类型传输模型
     */
    public FoodTypeDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        FoodTypeDto FoodTypeDto = new FoodTypeDto();
       
        BeanUtils.copyProperties(FoodTypeDto,this);
       
        return FoodTypeDto;
    }

}
