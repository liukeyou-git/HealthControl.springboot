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
 * 运动参考表
 */
@Data
@TableName("`Sport`")
public class Sport extends BaseEntity {

      
  	  /**
     * 介绍
     */  
    @JsonProperty("Content")
    @TableField(value="Content",updateStrategy = FieldStrategy.ALWAYS)
    private String Content;
      
  	  /**
     * 运动名称
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
     * 把运动参考实体转换成运动参考传输模型
     */
    public SportDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        SportDto SportDto = new SportDto();
       
        BeanUtils.copyProperties(SportDto,this);
       
        return SportDto;
    }

}
