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
 * 消息通知类
 */
@Data
public class MessageNoticeDto extends BaseDto
{

    
     
    /**
     * 通知标题
     */ 
    @JsonProperty("Title")
    private String Title;
    
     
    /**
     * 通知内容
     */ 
    @JsonProperty("Content")
    private String Content;
    
     
    /**
     * 类型
     */ 
    @JsonProperty("Type")
    private String Type;
    
     
    /**
     * 目标
     */ 
    @JsonProperty("TargetKey")
    private String TargetKey;
    
     
    /**
     * 计划发送时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("PlanSendTime")
    private LocalDateTime PlanSendTime;             
    
     
    /**
     * 实际发送时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("ActualSendTime")
    private LocalDateTime ActualSendTime;             
    
     
    /**
     * 接受人
     */ 
    @JsonProperty("UserId")
    private Integer UserId;          
    
     
    /**
     * 是否发送
     */ 
    @JsonProperty("IsSend")
    private Boolean IsSend;          
    
     
    /**
     * 是否成功
     */ 
    @JsonProperty("IsSuccess")
    private Boolean IsSuccess;          
    
     
    /**
     * 发送结果
     */ 
    @JsonProperty("ResultMsg")
    private String ResultMsg;
    
     
    /**
     * 关联单号
     */ 
    @JsonProperty("RelationNo")
    private String RelationNo;

     @JsonProperty("UserDto") 
    private AppUserDto UserDto;                        
   
 	 /**
     * 把消息通知传输模型转换成消息通知实体
     */
    public MessageNotice MapToEntity() throws InvocationTargetException, IllegalAccessException {
        MessageNotice MessageNotice= new MessageNotice();
     
         BeanUtils.copyProperties(MessageNotice,this);
        
        return MessageNotice;
    }

}
