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
 * 消息通知查询模型
 */
@NoArgsConstructor
@Data
public class MessageNoticePagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 通知标题模糊查询条件
     */
  	 @JsonProperty("Title")
    private String Title;
    /**
     * 通知内容模糊查询条件
     */
  	 @JsonProperty("Content")
    private String Content;
    /**
     * 类型模糊查询条件
     */
  	 @JsonProperty("Type")
    private String Type;
    /**
     * 目标模糊查询条件
     */
  	 @JsonProperty("TargetKey")
    private String TargetKey;
    /**
     * 发送结果模糊查询条件
     */
  	 @JsonProperty("ResultMsg")
    private String ResultMsg;
    /**
     * 关联单号模糊查询条件
     */
  	 @JsonProperty("RelationNo")
    private String RelationNo;
     /**
     * 接受人
     */
  	 @JsonProperty("UserId")
    private Integer UserId;
    /**
     * 计划发送时间时间范围
     */
    @JsonProperty("PlanSendTimeRange")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> PlanSendTimeRange;
    /**
     * 实际发送时间时间范围
     */
    @JsonProperty("ActualSendTimeRange")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> ActualSendTimeRange;
     /**
     * 是否发送
     */
  	 @JsonProperty("IsSend")
    private Boolean IsSend;
     /**
     * 是否成功
     */
  	 @JsonProperty("IsSuccess")
    private Boolean IsSuccess;

}
