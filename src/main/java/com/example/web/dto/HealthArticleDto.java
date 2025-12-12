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
 * 健康知识类
 */
@Data
public class HealthArticleDto extends BaseDto
{

    
     
    /**
     * 标题
     */ 
    @JsonProperty("Title")
    private String Title;
    
     
    /**
     * 分类
     */ 
    @JsonProperty("HealthArticleTypeId")
    private Integer HealthArticleTypeId;          
    
     
    /**
     * 封面
     */ 
    @JsonProperty("Cover")
    private String Cover;
    
     
    /**
     * 发布人
     */ 
    @JsonProperty("PublishUserId")
    private Integer PublishUserId;          
    
     
    /**
     * 内容
     */ 
    @JsonProperty("Content")
    private String Content;
    
     
    /**
     * 浏览量
     */ 
    @JsonProperty("ViewCount")
    private Integer ViewCount;          
    
     
    /**
     * 审核状态
     */ 
    @JsonProperty("AuditStatus")
    private Integer AuditStatus;    
    
    public String getAuditStatusFormat() {
        return AuditStatusEnum.GetEnum(AuditStatus).toString();
    }
    
    private String AuditStatusFormat;
    
    
     
    /**
     * 审核时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("AuditTime")
    private LocalDateTime AuditTime;             
    
     
    /**
     * 审核人
     */ 
    @JsonProperty("AuditUserId")
    private Integer AuditUserId;          

     @JsonProperty("PublishUserDto") 
    private AppUserDto PublishUserDto;                        
   
     @JsonProperty("AuditUserDto") 
    private AppUserDto AuditUserDto;                        
   
     @JsonProperty("HealthArticleTypeDto") 
    private HealthArticleTypeDto HealthArticleTypeDto;                        
   
 	 /**
     * 把健康知识传输模型转换成健康知识实体
     */
    public HealthArticle MapToEntity() throws InvocationTargetException, IllegalAccessException {
        HealthArticle HealthArticle= new HealthArticle();
     
         BeanUtils.copyProperties(HealthArticle,this);
        
        return HealthArticle;
    }

}
