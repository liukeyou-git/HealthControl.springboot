package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import lombok.SneakyThrows;
import com.example.web.tools.*;

import java.time.LocalDateTime;

/**
 * 运动记录功能实现类
 */
@Service
public class SportRecordServiceImpl extends ServiceImpl<SportRecordMapper, SportRecord> implements SportRecordService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的SportRecord表mapper对象
     */
    @Autowired
    private SportRecordMapper SportRecordMapper;
    @Autowired
    private SportMapper SportMapper;
    @Autowired
    private SportUnitMapper SportUnitMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<SportRecord> BuilderQuery(SportRecordPagedInput input) {
        // 声明一个支持运动记录查询的(拉姆达)表达式
        LambdaQueryWrapper<SportRecord> queryWrapper = Wrappers.<SportRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, SportRecord::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件

        if (input.getSportId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getSportId, input.getSportId());
        }

        if (input.getSportUnitId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getSportUnitId, input.getSportUnitId());
        }

        if (input.getRecordUserId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getRecordUserId, input.getRecordUserId());
        }
        if (input.getRecordTimeRange() != null && !input.getRecordTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(SportRecord::getRecordTime, input.getRecordTimeRange().get(1));
            queryWrapper = queryWrapper.ge(SportRecord::getRecordTime, input.getRecordTimeRange().get(0));
        }

        return queryWrapper;
    }

    /**
     * 处理运动记录对于的外键数据
     */
    private List<SportRecordDto> DispatchItem(List<SportRecordDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (SportRecordDto item : items) {

            // 查询出关联的AppUser表信息
            AppUser RecordUserEntity = AppUserMapper.selectById(item.getRecordUserId());
            item.setRecordUserDto(RecordUserEntity != null ? RecordUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的Sport表信息
            Sport SportEntity = SportMapper.selectById(item.getSportId());
            item.setSportDto(SportEntity != null ? SportEntity.MapToDto() : new SportDto());

            // 查询出关联的SportUnit表信息
            SportUnit SportUnitEntity = SportUnitMapper.selectById(item.getSportUnitId());
            item.setSportUnitDto(SportUnitEntity != null ? SportUnitEntity.MapToDto() : new SportUnitDto());
        }

        return items;
    }

    /**
     * 运动记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<SportRecordDto> List(SportRecordPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<SportRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(SportRecord::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<SportRecord> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取运动记录数据
        IPage<SportRecord> pageRecords = SportRecordMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = SportRecordMapper.selectCount(queryWrapper);
        // 把SportRecord实体转换成SportRecord传输模型
        List<SportRecordDto> items = Extension.copyBeanList(pageRecords.getRecords(), SportRecordDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个运动记录查询
     */
    @SneakyThrows
    @Override
    public SportRecordDto Get(SportRecordPagedInput input) {
        if (input.getId() == null) {
            return new SportRecordDto();
        }

        PagedResult<SportRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new SportRecordDto());
    }

    /**
     * 运动记录创建或者修改
     */
    @SneakyThrows
    @Override
    public SportRecordDto CreateOrEdit(SportRecordDto input) {
        // 声明一个运动记录实体
        SportRecord SportRecord = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(SportRecord);
        // 把传输模型返回给前端
        return SportRecord.MapToDto();
    }

    /**
     * 运动记录删除
     */
    @Override
    public void Delete(IdInput input) {
        SportRecord entity = SportRecordMapper.selectById(input.getId());
        SportRecordMapper.deleteById(entity);
    }

    /**
     * 运动记录批量删除
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
     * 用户运动统计
     *
     * @param input
     * @return
     */
    @Override
    public SportRecordSummaryDto SportRecordSummary(SportRecordPagedInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        SportRecordSummaryDto sportRecordSummaryDto = new SportRecordSummaryDto();

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 今日统计 (00:00:00 到 23:59:59)
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusSeconds(1);

        // 本周统计 (周一到周日)
        LocalDateTime weekStart = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1).minusSeconds(1);

        // 本月统计 (月初到月末)
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

        // 今日统计
        double todayCalories = calculateCaloriesForPeriod(userId, todayStart, todayEnd);

        // 本周统计
        double weekCalories = calculateCaloriesForPeriod(userId, weekStart, weekEnd);
        int weekCount = calculateSportCountForPeriod(userId, weekStart, weekEnd);

        // 本月统计
        double monthCalories = calculateCaloriesForPeriod(userId, monthStart, monthEnd);
        int monthCount = calculateSportCountForPeriod(userId, monthStart, monthEnd);

        // 设置统计结果
        sportRecordSummaryDto.setTotalCalories(Extension.ToFixed4(todayCalories));
        sportRecordSummaryDto.setTotalCaloriesWeek(Extension.ToFixed4(weekCalories));
        sportRecordSummaryDto.setTotalSportCountWeek(weekCount);
        sportRecordSummaryDto.setTotalSportCountMonth(monthCount);
        sportRecordSummaryDto.setTotalCaloriesMonth(Extension.ToFixed4(monthCalories));

        return sportRecordSummaryDto;
    }

    /**
     * 计算指定时间段内的卡路里消耗
     */
    private double calculateCaloriesForPeriod(Integer userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件
        LambdaQueryWrapper<SportRecord> queryWrapper = Wrappers.<SportRecord>lambdaQuery()
                .eq(SportRecord::getRecordUserId, userId)
                .ge(SportRecord::getRecordTime, startTime)
                .le(SportRecord::getRecordTime, endTime);

        // 查询运动记录
        List<SportRecord> records = SportRecordMapper.selectList(queryWrapper);

        double totalCalories = 0.0;

        for (SportRecord record : records) {
            if (record.getSportUnitId() != null && record.getRecordValue() != null) {
                // 获取运动单位信息
                SportUnit sportUnit = SportUnitMapper.selectById(record.getSportUnitId());
                if (sportUnit != null && sportUnit.getCalories() != null) {
                    // 计算卡路里：单位卡路里 * 记录值
                    totalCalories += sportUnit.getCalories() * record.getRecordValue();
                }
            }
        }

        return totalCalories;
    }

    /**
     * 计算指定时间段内的运动次数
     */
    private int calculateSportCountForPeriod(Integer userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件
        LambdaQueryWrapper<SportRecord> queryWrapper = Wrappers.<SportRecord>lambdaQuery()
                .eq(SportRecord::getRecordUserId, userId)
                .ge(SportRecord::getRecordTime, startTime)
                .le(SportRecord::getRecordTime, endTime);

        // 统计运动记录数量
        Long count = SportRecordMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }
}
