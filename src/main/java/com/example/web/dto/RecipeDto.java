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
 * 食谱类
 */
@Data
public class RecipeDto extends BaseDto
{

    
     
    /**
     * 标题
     */ 
    @JsonProperty("Title")
    private String Title;
    
     
    /**
     * 封面
     */ 
    @JsonProperty("Cover")
    private String Cover;
    
     
    /**
     * 详细图
     */ 
    @JsonProperty("ImageUrls")
    private String ImageUrls;
    
     
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
     * 视频路径
     */ 
    @JsonProperty("VideoUrl")
    private String VideoUrl;
    
     
    /**
     * 审核人
     */ 
    @JsonProperty("AuditUserId")
    private Integer AuditUserId;          
    
     
    /**
     * 发布人
     */ 
    @JsonProperty("PublishUserId")
    private Integer PublishUserId;          
    
     
    /**
     * 审核时间
     */ 
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("AuditTime")
    private LocalDateTime AuditTime;             
    
     
    /**
     * 审核状态
     */ 
    @JsonProperty("AuditStatus")
    private Integer AuditStatus;    
    
    public String getAuditStatusFormat() {
        return AuditStatusEnum.GetEnum(AuditStatus).toString();
    }
    
    private String AuditStatusFormat;
    

     @JsonProperty("AuditUserDto") 
    private AppUserDto AuditUserDto;                        
   
     @JsonProperty("PublishUserDto") 
    private AppUserDto PublishUserDto;                        
   
 	 /**
     * 把食谱传输模型转换成食谱实体
     */
    public Recipe MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Recipe Recipe= new Recipe();
     
         BeanUtils.copyProperties(Recipe,this);
        
        return Recipe;
    }

}
