package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康指标记录表对应的Mapper
 */
@Mapper
public interface HealthIndicatorRecordMapper  extends BaseMapper<HealthIndicatorRecord> {

}
