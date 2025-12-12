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
 * 食谱查询模型
 */
@NoArgsConstructor
@Data
public class RecipePagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 标题模糊查询条件
     */
  	 @JsonProperty("Title")
    private String Title;
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
     * 审核状态
     */
  	 @JsonProperty("AuditStatus")
    private Integer AuditStatus;
    /**
     * 审核时间时间范围
     */
    @JsonProperty("AuditTimeRange")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> AuditTimeRange;

}
