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
 * 运动记录表
 */
@Data
@TableName("`SportRecord`")
public class SportRecord extends BaseEntity {

      
    /**
     * 运动
     */  
    @JsonProperty("SportId")
    @TableField(value="SportId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer SportId;          
      
    /**
     * 运动单位
     */  
    @JsonProperty("SportUnitId")
    @TableField(value="SportUnitId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer SportUnitId;          
      
    /**
     * 记录人
     */  
    @JsonProperty("RecordUserId")
    @TableField(value="RecordUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer RecordUserId;          
      
    /**
     * 记录时间
     */  
    @JsonProperty("RecordTime")
    @TableField(value="RecordTime",updateStrategy = FieldStrategy.ALWAYS)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime RecordTime;             
      
    /**
     * 记录值
     */  
    @JsonProperty("RecordValue")
    @TableField(value="RecordValue",updateStrategy = FieldStrategy.ALWAYS)
    private Integer RecordValue;          
  
    /**
     * 把运动记录实体转换成运动记录传输模型
     */
    public SportRecordDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        SportRecordDto SportRecordDto = new SportRecordDto();
       
        BeanUtils.copyProperties(SportRecordDto,this);
       
        return SportRecordDto;
    }

}
