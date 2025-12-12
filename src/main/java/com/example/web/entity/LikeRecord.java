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
 * 点赞记录表
 */
@Data
@TableName("`LikeRecord`")
public class LikeRecord extends BaseEntity {

      
    /**
     * 点赞人
     */  
    @JsonProperty("LikeUserId")
    @TableField(value="LikeUserId",updateStrategy = FieldStrategy.ALWAYS)
    private Integer LikeUserId;          
      
  	  /**
     * 点赞类型
     */  
    @JsonProperty("LikeType")
    @TableField(value="LikeType",updateStrategy = FieldStrategy.ALWAYS)
    private String LikeType;
      
  	  /**
     * 关联
     */  
    @JsonProperty("RelativeId")
    @TableField(value="RelativeId",updateStrategy = FieldStrategy.ALWAYS)
    private String RelativeId;
  
    /**
     * 把点赞记录实体转换成点赞记录传输模型
     */
    public LikeRecordDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        LikeRecordDto LikeRecordDto = new LikeRecordDto();
       
        BeanUtils.copyProperties(LikeRecordDto,this);
       
        return LikeRecordDto;
    }

}
