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
 * 消息通知表
 */
@Data
@TableName("`MessageNotice`")
public class MessageNotice extends BaseEntity {

      
  	  /**
     * 通知标题
     */  
    @JsonProperty("Title")
    @TableField(value="Title",updateStrategy = FieldStrategy.IGNORED)
    private String Title;
      
  	  /**
     * 通知内容
     */  
    @JsonProperty("Content")
    @TableField(value="Content",updateStrategy = FieldStrategy.IGNORED)
    private String Content;
      
  	  /**
     * 类型
     */  
    @JsonProperty("Type")
    @TableField(value="Type",updateStrategy = FieldStrategy.IGNORED)
    private String Type;
      
  	  /**
     * 目标
     */  
    @JsonProperty("TargetKey")
    @TableField(value="TargetKey",updateStrategy = FieldStrategy.IGNORED)
    private String TargetKey;
      
    /**
     * 计划发送时间
     */  
    @JsonProperty("PlanSendTime")
    @TableField(value="PlanSendTime",updateStrategy = FieldStrategy.IGNORED)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime PlanSendTime;             
      
    /**
     * 实际发送时间
     */  
    @JsonProperty("ActualSendTime")
    @TableField(value="ActualSendTime",updateStrategy = FieldStrategy.IGNORED)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime ActualSendTime;             
      
    /**
     * 接受人
     */  
    @JsonProperty("UserId")
    @TableField(value="UserId",updateStrategy = FieldStrategy.IGNORED)
    private Integer UserId;          
      
    /**
     * 是否发送
     */  
    @JsonProperty("IsSend")
    @TableField(value="IsSend",updateStrategy = FieldStrategy.IGNORED)
    private Boolean IsSend;          
      
    /**
     * 是否成功
     */  
    @JsonProperty("IsSuccess")
    @TableField(value="IsSuccess",updateStrategy = FieldStrategy.IGNORED)
    private Boolean IsSuccess;          
      
  	  /**
     * 发送结果
     */  
    @JsonProperty("ResultMsg")
    @TableField(value="ResultMsg",updateStrategy = FieldStrategy.IGNORED)
    private String ResultMsg;
      
  	  /**
     * 关联单号
     */  
    @JsonProperty("RelationNo")
    @TableField(value="RelationNo",updateStrategy = FieldStrategy.IGNORED)
    private String RelationNo;
  
    /**
     * 把消息通知实体转换成消息通知传输模型
     */
    public MessageNoticeDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        MessageNoticeDto MessageNoticeDto = new MessageNoticeDto();
       
        BeanUtils.copyProperties(MessageNoticeDto,this);
       
        return MessageNoticeDto;
    }

}
