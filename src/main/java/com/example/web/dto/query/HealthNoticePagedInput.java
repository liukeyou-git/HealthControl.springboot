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
 * 健康提醒查询模型
 */
@NoArgsConstructor
@Data
public class HealthNoticePagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 提醒内容模糊查询条件
     */
  	 @JsonProperty("Content")
    private String Content;
    /**
     * 提醒标题模糊查询条件
     */
  	 @JsonProperty("Title")
    private String Title;
    /**
     * 提醒方式模糊查询条件
     */
  	 @JsonProperty("RemindType")
    private String RemindType;
     /**
     * 提醒人
     */
  	 @JsonProperty("PublishUserId")
    private Integer PublishUserId;
    /**
     * 提醒时间时间范围
     */
    @JsonProperty("RemindTimeRange")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> RemindTimeRange;
     /**
     * 是否提醒
     */
  	 @JsonProperty("IsRemind")
    private Boolean IsRemind;

}
