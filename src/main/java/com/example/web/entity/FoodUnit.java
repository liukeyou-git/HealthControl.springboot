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
 * 食物单位表
 */
@Data
@TableName("`FoodUnit`")
public class FoodUnit extends BaseEntity {

      
    /**
     * 食物
     */  
    @JsonProperty("FoodId")
    @TableField(value="FoodId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer FoodId;          
      
  	  /**
     * 单位名称
     */  
    @JsonProperty("UnitName")
    @TableField(value="UnitName",updateStrategy = FieldStrategy.ALWAYS)
    private String UnitName;
      
    /**
     * 单位值
     */  
    @JsonProperty("UnitValue")
    @TableField(value="UnitValue",updateStrategy = FieldStrategy.ALWAYS)
    private Integer UnitValue;          
  
    /**
     * 把食物单位实体转换成食物单位传输模型
     */
    public FoodUnitDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        FoodUnitDto FoodUnitDto = new FoodUnitDto();
       
        BeanUtils.copyProperties(FoodUnitDto,this);
       
        return FoodUnitDto;
    }

}
