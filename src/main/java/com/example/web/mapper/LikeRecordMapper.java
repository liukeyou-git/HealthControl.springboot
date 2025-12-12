package com.example.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞记录表对应的Mapper
 */
@Mapper
public interface LikeRecordMapper  extends BaseMapper<LikeRecord> {

}
