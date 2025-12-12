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
 * 运动单位查询模型
 */
@NoArgsConstructor
@Data
public class SportUnitPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 单位名称模糊查询条件
     */
  	 @JsonProperty("UnitName")
    private String UnitName;
     /**
     * 运动
     */
  	 @JsonProperty("SportId")
    private Integer SportId;

}
