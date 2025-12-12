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
 * 收藏记录类
 */
@Data
public class CollectRecordDto extends BaseDto
{

    
     
    /**
     * 收藏人
     */ 
    @JsonProperty("CollectUserId")
    private Integer CollectUserId;          
    
     
    /**
     * 收藏类型
     */ 
    @JsonProperty("CollectType")
    private String CollectType;
    
     
    /**
     * 关联资源
     */ 
    @JsonProperty("RelativeId")
    private Integer RelativeId;          

     @JsonProperty("CollectUserDto") 
    private AppUserDto CollectUserDto;                        
   
 	 /**
     * 把收藏记录传输模型转换成收藏记录实体
     */
    public CollectRecord MapToEntity() throws InvocationTargetException, IllegalAccessException {
        CollectRecord CollectRecord= new CollectRecord();
     
         BeanUtils.copyProperties(CollectRecord,this);
        
        return CollectRecord;
    }

}
