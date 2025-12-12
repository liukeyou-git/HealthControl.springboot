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
 * 健康指标分类查询模型
 */
@NoArgsConstructor
@Data
public class HealthIndicatorTypePagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 分类名称模糊查询条件
     */
  	 @JsonProperty("Name")
    private String Name;
     /**
     * 所属人
     */
  	 @JsonProperty("BelongUserId")
    private Integer BelongUserId;
     /**
     * 是否公用
     */
  	 @JsonProperty("IsComm")
    private Boolean IsComm;

}
