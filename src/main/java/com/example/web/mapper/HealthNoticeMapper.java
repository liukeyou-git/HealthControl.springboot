package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康提醒表对应的Mapper
 */
@Mapper
public interface HealthNoticeMapper  extends BaseMapper<HealthNotice> {

}
