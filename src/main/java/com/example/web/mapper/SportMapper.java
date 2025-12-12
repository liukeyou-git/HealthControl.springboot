package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运动参考表对应的Mapper
 */
@Mapper
public interface SportMapper  extends BaseMapper<Sport> {

}
