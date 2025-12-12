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
 * 点赞记录类
 */
@Data
public class LikeRecordDto extends BaseDto
{

    
     
    /**
     * 点赞人
     */ 
    @JsonProperty("LikeUserId")
    private Integer LikeUserId;          
    
     
    /**
     * 点赞类型
     */ 
    @JsonProperty("LikeType")
    private String LikeType;
    
     
    /**
     * 关联
     */ 
    @JsonProperty("RelativeId")
    private String RelativeId;

     @JsonProperty("LikeUserDto") 
    private AppUserDto LikeUserDto;                        
   
 	 /**
     * 把点赞记录传输模型转换成点赞记录实体
     */
    public LikeRecord MapToEntity() throws InvocationTargetException, IllegalAccessException {
        LikeRecord LikeRecord= new LikeRecord();
     
         BeanUtils.copyProperties(LikeRecord,this);
        
        return LikeRecord;
    }

}
