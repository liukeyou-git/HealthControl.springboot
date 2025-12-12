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
 * 健康提醒表
 */
@Data
@TableName("`HealthNotice`")
public class HealthNotice extends BaseEntity {

      
    /**
     * 提醒人
     */  
    @JsonProperty("PublishUserId")
    @TableField(value="PublishUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer PublishUserId;          
      
  	  /**
     * 提醒内容
     */  
    @JsonProperty("Content")
    @TableField(value="Content",updateStrategy = FieldStrategy.ALWAYS)
    private String Content;
      
  	  /**
     * 提醒标题
     */  
    @JsonProperty("Title")
    @TableField(value="Title",updateStrategy = FieldStrategy.ALWAYS)
    private String Title;
      
    /**
     * 提醒次数
     */  
    @JsonProperty("Num")
    @TableField(value="Num",updateStrategy = FieldStrategy.ALWAYS)
    private Integer Num;          
      
    /**
     * 提醒时间
     */  
    @JsonProperty("RemindTime")
    @TableField(value="RemindTime",updateStrategy = FieldStrategy.ALWAYS)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime RemindTime;             
      
  	  /**
     * 提醒方式
     */  
    @JsonProperty("RemindType")
    @TableField(value="RemindType",updateStrategy = FieldStrategy.ALWAYS)
    private String RemindType;
      
    /**
     * 是否提醒
     */  
    @JsonProperty("IsRemind")
    @TableField(value="IsRemind",updateStrategy = FieldStrategy.ALWAYS)
    private Boolean IsRemind;          
  
    /**
     * 把健康提醒实体转换成健康提醒传输模型
     */
    public HealthNoticeDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        HealthNoticeDto HealthNoticeDto = new HealthNoticeDto();
       
        BeanUtils.copyProperties(HealthNoticeDto,this);
       
        return HealthNoticeDto;
    }

}
