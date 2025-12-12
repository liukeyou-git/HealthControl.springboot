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
 * 健康知识分类类
 */
@Data
public class HealthArticleTypeDto extends BaseDto
{

    
     
    /**
     * 分类名称
     */ 
    @JsonProperty("Name")
    private String Name;
    
     
    /**
     * 显示顺序
     */ 
    @JsonProperty("Sort")
    private Integer Sort;          

 	 /**
     * 把健康知识分类传输模型转换成健康知识分类实体
     */
    public HealthArticleType MapToEntity() throws InvocationTargetException, IllegalAccessException {
        HealthArticleType HealthArticleType= new HealthArticleType();
     
         BeanUtils.copyProperties(HealthArticleType,this);
        
        return HealthArticleType;
    }

}
