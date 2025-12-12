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
 * 食物单位查询模型
 */
@NoArgsConstructor
@Data
public class FoodUnitPagedInput extends PagedInput {
    
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
     * 食物
     */
  	 @JsonProperty("FoodId")
    private Integer FoodId;

}
