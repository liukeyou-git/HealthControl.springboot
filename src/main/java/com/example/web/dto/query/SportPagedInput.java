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
 * 运动参考查询模型
 */
@NoArgsConstructor
@Data
public class SportPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 介绍模糊查询条件
     */
  	 @JsonProperty("Content")
    private String Content;
    /**
     * 运动名称模糊查询条件
     */
  	 @JsonProperty("Name")
    private String Name;

}
