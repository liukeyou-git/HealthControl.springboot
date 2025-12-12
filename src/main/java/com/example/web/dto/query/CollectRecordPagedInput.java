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
 * 收藏记录查询模型
 */
@NoArgsConstructor
@Data
public class CollectRecordPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 收藏类型模糊查询条件
     */
  	 @JsonProperty("CollectType")
    private String CollectType;
     /**
     * 收藏人
     */
  	 @JsonProperty("CollectUserId")
    private Integer CollectUserId;

}
