package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食物表对应的Mapper
 */
@Mapper
public interface FoodMapper  extends BaseMapper<Food> {

}
