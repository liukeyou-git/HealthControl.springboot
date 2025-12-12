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
 * 运动记录查询模型
 */
@NoArgsConstructor
@Data
public class SportRecordPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
     /**
     * 运动
     */
  	 @JsonProperty("SportId")
    private Integer SportId;
     /**
     * 运动单位
     */
  	 @JsonProperty("SportUnitId")
    private Integer SportUnitId;
     /**
     * 记录人
     */
  	 @JsonProperty("RecordUserId")
    private Integer RecordUserId;
    /**
     * 记录时间时间范围
     */
    @JsonProperty("RecordTimeRange")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> RecordTimeRange;

}
