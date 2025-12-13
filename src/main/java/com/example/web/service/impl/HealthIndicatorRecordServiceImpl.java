package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import com.example.web.tools.exception.CustomException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.tools.*;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 健康指标记录功能实现类
 */
@Service
public class HealthIndicatorRecordServiceImpl extends ServiceImpl<HealthIndicatorRecordMapper, HealthIndicatorRecord>
        implements HealthIndicatorRecordService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthIndicatorRecord表mapper对象
     */
    @Autowired
    private HealthIndicatorRecordMapper HealthIndicatorRecordMapper;
    @Autowired
    private HealthIndicatorMapper HealthIndicatorMapper;
    @Autowired
    private HealthIndicatorTypeMapper HealthIndicatorTypeMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthIndicatorRecord> BuilderQuery(HealthIndicatorRecordPagedInput input) {
        // 声明一个支持健康指标记录查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthIndicatorRecord> queryWrapper = Wrappers.<HealthIndicatorRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthIndicatorRecord::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getIsAbnormity())) {
            queryWrapper = queryWrapper.like(HealthIndicatorRecord::getIsAbnormity, input.getIsAbnormity());
        }

        if (input.getHealthIndicatorTypeId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicatorRecord::getHealthIndicatorTypeId,
                    input.getHealthIndicatorTypeId());
        }

        if (input.getHealthIndicatorId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicatorRecord::getHealthIndicatorId, input.getHealthIndicatorId());
        }

        if (input.getRecordUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicatorRecord::getRecordUserId, input.getRecordUserId());
        }
        if (input.getRecordTimeRange() != null && !input.getRecordTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(HealthIndicatorRecord::getRecordTime, input.getRecordTimeRange().get(1));
            queryWrapper = queryWrapper.ge(HealthIndicatorRecord::getRecordTime, input.getRecordTimeRange().get(0));
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(HealthIndicatorRecord::getIsAbnormity, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理健康指标记录对于的外键数据
     */
    private List<HealthIndicatorRecordDto> DispatchItem(List<HealthIndicatorRecordDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (HealthIndicatorRecordDto item : items) {

            // 查询出关联的HealthIndicator表信息
            HealthIndicator HealthIndicatorEntity = HealthIndicatorMapper.selectById(item.getHealthIndicatorId());
            item.setHealthIndicatorDto(
                    HealthIndicatorEntity != null ? HealthIndicatorEntity.MapToDto() : new HealthIndicatorDto());

            // 查询出关联的AppUser表信息
            AppUser RecordUserEntity = AppUserMapper.selectById(item.getRecordUserId());
            item.setRecordUserDto(RecordUserEntity != null ? RecordUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的HealthIndicatorType表信息
            HealthIndicatorType HealthIndicatorTypeEntity = HealthIndicatorTypeMapper
                    .selectById(item.getHealthIndicatorTypeId());
            item.setHealthIndicatorTypeDto(HealthIndicatorTypeEntity != null ? HealthIndicatorTypeEntity.MapToDto()
                    : new HealthIndicatorTypeDto());
        }

        return items;
    }

    /**
     * 健康指标记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthIndicatorRecordDto> List(HealthIndicatorRecordPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<HealthIndicatorRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthIndicatorRecord::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<HealthIndicatorRecord> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取健康指标记录数据
        IPage<HealthIndicatorRecord> pageRecords = HealthIndicatorRecordMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = HealthIndicatorRecordMapper.selectCount(queryWrapper);
        // 把HealthIndicatorRecord实体转换成HealthIndicatorRecord传输模型
        List<HealthIndicatorRecordDto> items = Extension.copyBeanList(pageRecords.getRecords(),
                HealthIndicatorRecordDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个健康指标记录查询
     */
    @SneakyThrows
    @Override
    public HealthIndicatorRecordDto Get(HealthIndicatorRecordPagedInput input) {
        if (input.getId() == null) {
            return new HealthIndicatorRecordDto();
        }

        PagedResult<HealthIndicatorRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthIndicatorRecordDto());
    }

    /**
     * 健康指标记录创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthIndicatorRecordDto CreateOrEdit(HealthIndicatorRecordDto input) {
        // 声明一个健康指标记录实体
        HealthIndicatorRecord HealthIndicatorRecord = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(HealthIndicatorRecord);
        // 把传输模型返回给前端
        return HealthIndicatorRecord.MapToDto();
    }

    /**
     * 健康指标记录删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthIndicatorRecord entity = HealthIndicatorRecordMapper.selectById(input.getId());
        HealthIndicatorRecordMapper.deleteById(entity);
    }

    /**
     * 健康指标记录批量删除
     */
    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            Delete(idInput);
        }
    }

    /**
     * 健康指标记录批量创建
     */
    @SneakyThrows
    @Override
    public void BatchAdd(List<HealthIndicatorRecordDto> input) {

        for (HealthIndicatorRecordDto item : input) {
            CreateOrEdit(item);
        }
    }
}
