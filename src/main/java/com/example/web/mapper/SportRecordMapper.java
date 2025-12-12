package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运动记录表对应的Mapper
 */
@Mapper
public interface SportRecordMapper  extends BaseMapper<SportRecord> {

}
