package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食记录表对应的Mapper
 */
@Mapper
public interface DietRecordMapper  extends BaseMapper<DietRecord> {

}
