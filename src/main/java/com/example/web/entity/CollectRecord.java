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
 * 收藏记录表
 */
@Data
@TableName("`CollectRecord`")
public class CollectRecord extends BaseEntity {

      
    /**
     * 收藏人
     */  
    @JsonProperty("CollectUserId")
    @TableField(value="CollectUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer CollectUserId;          
      
  	  /**
     * 收藏类型
     */  
    @JsonProperty("CollectType")
    @TableField(value="CollectType",updateStrategy = FieldStrategy.ALWAYS)
    private String CollectType;
      
    /**
     * 关联资源
     */  
    @JsonProperty("RelativeId")
    @TableField(value="RelativeId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer RelativeId;          
  
    /**
     * 把收藏记录实体转换成收藏记录传输模型
     */
    public CollectRecordDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        CollectRecordDto CollectRecordDto = new CollectRecordDto();
       
        BeanUtils.copyProperties(CollectRecordDto,this);
       
        return CollectRecordDto;
    }

}
