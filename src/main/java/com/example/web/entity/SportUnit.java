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
 * 运动单位表
 */
@Data
@TableName("`SportUnit`")
public class SportUnit extends BaseEntity {

      
    /**
     * 运动
     */  
    @JsonProperty("SportId")
    @TableField(value="SportId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer SportId;          
      
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
    private Double UnitValue;      
      
    /**
     * 热量
     */  
    @JsonProperty("Calories")
    @TableField(value="Calories",updateStrategy = FieldStrategy.ALWAYS)
    private Double Calories;      
  
    /**
     * 把运动单位实体转换成运动单位传输模型
     */
    public SportUnitDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        SportUnitDto SportUnitDto = new SportUnitDto();
       
        BeanUtils.copyProperties(SportUnitDto,this);
       
        return SportUnitDto;
    }

}
