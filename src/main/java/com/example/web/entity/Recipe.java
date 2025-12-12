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
 * 食谱表
 */
@Data
@TableName("`Recipe`")
public class Recipe extends BaseEntity {

      
  	  /**
     * 标题
     */  
    @JsonProperty("Title")
    @TableField(value="Title",updateStrategy = FieldStrategy.ALWAYS)
    private String Title;
      
  	  /**
     * 封面
     */  
    @JsonProperty("Cover")
    @TableField(value="Cover",updateStrategy = FieldStrategy.ALWAYS)
    private String Cover;
      
  	  /**
     * 详细图
     */  
    @JsonProperty("ImageUrls")
    @TableField(value="ImageUrls",updateStrategy = FieldStrategy.ALWAYS)
    private String ImageUrls;
      
    /**
     * 内容
     */  
    @JsonProperty("Content")
     @TableField(value="Content",updateStrategy = FieldStrategy.ALWAYS)
    private String Content;
      
    /**
     * 浏览量
     */  
    @JsonProperty("ViewCount")
    @TableField(value="ViewCount",updateStrategy = FieldStrategy.ALWAYS)
    private Integer ViewCount;          
      
  	  /**
     * 视频路径
     */  
    @JsonProperty("VideoUrl")
    @TableField(value="VideoUrl",updateStrategy = FieldStrategy.ALWAYS)
    private String VideoUrl;
      
    /**
     * 审核人
     */  
    @JsonProperty("AuditUserId")
    @TableField(value="AuditUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer AuditUserId;          
      
    /**
     * 发布人
     */  
    @JsonProperty("PublishUserId")
    @TableField(value="PublishUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer PublishUserId;          
      
    /**
     * 审核时间
     */  
    @JsonProperty("AuditTime")
    @TableField(value="AuditTime",updateStrategy = FieldStrategy.ALWAYS)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime AuditTime;             
      
    /**
     * 审核状态
     */  
    @JsonProperty("AuditStatus")
    @TableField(value="AuditStatus",updateStrategy = FieldStrategy.ALWAYS)
    private Integer AuditStatus;          
  
    /**
     * 把食谱实体转换成食谱传输模型
     */
    public RecipeDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        RecipeDto RecipeDto = new RecipeDto();
       
        BeanUtils.copyProperties(RecipeDto,this);
       
        return RecipeDto;
    }

}
