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
 * 食物查询模型
 */
@NoArgsConstructor
@Data
public class FoodPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 食物名称模糊查询条件
     */
  	 @JsonProperty("Name")
    private String Name;
     /**
     * 食物类型
     */
  	 @JsonProperty("FoodTypeId")
    private Integer FoodTypeId;

}
