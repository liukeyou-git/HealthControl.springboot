package com.example.web.dto.query;

import com.example.web.tools.dto.PagedInput;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * 点赞记录查询模型
 */
@NoArgsConstructor
@Data
public class LikeRecordPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 点赞类型模糊查询条件
     */
  	 @JsonProperty("LikeType")
    private String LikeType;
    /**
     * 关联模糊查询条件
     */
  	 @JsonProperty("RelativeId")
    private String RelativeId;
     /**
     * 点赞人
     */
  	 @JsonProperty("LikeUserId")
    private Integer LikeUserId;

}
