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
 * 饮食记录功能实现类
 */
@Service
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的DietRecord表mapper对象
     */
    @Autowired
    private DietRecordMapper DietRecordMapper;
    @Autowired
    private FoodMapper FoodMapper;
    @Autowired
    private FoodUnitMapper FoodUnitMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<DietRecord> BuilderQuery(DietRecordPagedInput input) {
        // 声明一个支持饮食记录查询的(拉姆达)表达式
        LambdaQueryWrapper<DietRecord> queryWrapper = Wrappers.<DietRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, DietRecord::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件

        if (input.getFoodId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getFoodId, input.getFoodId());
        }

        if (input.getRecordUserId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getRecordUserId, input.getRecordUserId());
        }

        if (input.getFoodUnitId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getFoodUnitId, input.getFoodUnitId());
        }
        if (input.getRecordTimeRange() != null && !input.getRecordTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(DietRecord::getRecordTime, input.getRecordTimeRange().get(1));
            queryWrapper = queryWrapper.ge(DietRecord::getRecordTime, input.getRecordTimeRange().get(0));
        }

        return queryWrapper;
    }

    /**
     * 处理饮食记录对于的外键数据
     */
    private List<DietRecordDto> DispatchItem(List<DietRecordDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (DietRecordDto item : items) {

            // 查询出关联的AppUser表信息
            AppUser RecordUserEntity = AppUserMapper.selectById(item.getRecordUserId());
            item.setRecordUserDto(RecordUserEntity != null ? RecordUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的Food表信息
            Food FoodEntity = FoodMapper.selectById(item.getFoodId());
            item.setFoodDto(FoodEntity != null ? FoodEntity.MapToDto() : new FoodDto());

            // 查询出关联的FoodUnit表信息
            FoodUnit FoodUnitEntity = FoodUnitMapper.selectById(item.getFoodUnitId());
            item.setFoodUnitDto(FoodUnitEntity != null ? FoodUnitEntity.MapToDto() : new FoodUnitDto());

            item.getFoodUnitDto().setProtein(
                    Extension.ToFixed4(item.getFoodDto().getProtein() * item.getFoodUnitDto().getUnitValue()));
            item.getFoodUnitDto().setCarbohydrates(
                    Extension.ToFixed4(item.getFoodDto().getCarbohydrates() * item.getFoodUnitDto().getUnitValue()));
            item.getFoodUnitDto()
                    .setFat(Extension.ToFixed4(item.getFoodDto().getFat() * item.getFoodUnitDto().getUnitValue()));
            item.getFoodUnitDto().setCalories(
                    Extension.ToFixed4(item.getFoodDto().getCalories() * item.getFoodUnitDto().getUnitValue()));

        }

        return items;
    }

    /**
     * 饮食记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<DietRecordDto> List(DietRecordPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<DietRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(DietRecord::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<DietRecord> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取饮食记录数据
        IPage<DietRecord> pageRecords = DietRecordMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = DietRecordMapper.selectCount(queryWrapper);
        // 把DietRecord实体转换成DietRecord传输模型
        List<DietRecordDto> items = Extension.copyBeanList(pageRecords.getRecords(), DietRecordDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个饮食记录查询
     */
    @SneakyThrows
    @Override
    public DietRecordDto Get(DietRecordPagedInput input) {
        if (input.getId() == null) {
            return new DietRecordDto();
        }

        PagedResult<DietRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new DietRecordDto());
    }

    /**
     * 饮食记录创建或者修改
     */
    @SneakyThrows
    @Override
    public DietRecordDto CreateOrEdit(DietRecordDto input) {
        // 声明一个饮食记录实体
        DietRecord DietRecord = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(DietRecord);
        // 把传输模型返回给前端
        return DietRecord.MapToDto();
    }

    /**
     * 饮食记录删除
     */
    @Override
    public void Delete(IdInput input) {
        DietRecord entity = DietRecordMapper.selectById(input.getId());
        DietRecordMapper.deleteById(entity);
    }

    /**
     * 饮食记录批量删除
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
     * 按早中晚来显示我的记录
     */
    @SneakyThrows
    @Override
    public List<DietRecordByDayDto> DietRecordByDay(DietRecordPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<DietRecord> queryWrapper = BuilderQuery(input);

        // 默认按创建时间从大到小排序
        queryWrapper = queryWrapper.orderByDesc(DietRecord::getCreationTime);

        List<DietRecord> pageRecords = DietRecordMapper.selectList(queryWrapper);

        // 把DietRecord实体转换成DietRecord传输模型
        List<DietRecordDto> items = Extension.copyBeanList(pageRecords, DietRecordDto.class);

        DispatchItem(items);

        // 按时间段分组：早餐(0-11点)、午餐(11-18点)、晚餐(18-24点)
        List<DietRecordDto> breakfastRecords = new ArrayList<>();
        List<DietRecordDto> lunchRecords = new ArrayList<>();
        List<DietRecordDto> dinnerRecords = new ArrayList<>();

        for (DietRecordDto item : items) {
            if (item.getRecordTime() != null) {
                int hour = item.getRecordTime().getHour();
                if (hour < 11) {
                    // 11点之前算早餐
                    breakfastRecords.add(item);
                } else if (hour >= 11 && hour < 18) {
                    // 11-18点算午餐
                    lunchRecords.add(item);
                } else {
                    // 18-24点算晚餐
                    dinnerRecords.add(item);
                }
            }
        }

        // 构建返回结果
        List<DietRecordByDayDto> result = new ArrayList<>();

        // 早餐
        if (!breakfastRecords.isEmpty()) {
            DietRecordByDayDto breakfast = new DietRecordByDayDto();
            breakfast.setDateType("早餐");
            breakfast.setDietRecordDtos(breakfastRecords);
            calculateNutritionTotals(breakfast, breakfastRecords);
            result.add(breakfast);
        }

        // 午餐
        if (!lunchRecords.isEmpty()) {
            DietRecordByDayDto lunch = new DietRecordByDayDto();
            lunch.setDateType("午餐");
            lunch.setDietRecordDtos(lunchRecords);
            calculateNutritionTotals(lunch, lunchRecords);
            result.add(lunch);
        }

        // 晚餐
        if (!dinnerRecords.isEmpty()) {
            DietRecordByDayDto dinner = new DietRecordByDayDto();
            dinner.setDateType("晚餐");
            dinner.setDietRecordDtos(dinnerRecords);
            calculateNutritionTotals(dinner, dinnerRecords);
            result.add(dinner);
        }

        return result;
    }

    /**
     * 按天来汇总我的摄入信息
     *
     * @param input 查询条件
     * @return 按天汇总的饮食记录列表
     */
    @SneakyThrows
    @Override
    public List<DietRecordByDaySummaryDto> DietRecordByDaySummary(DietRecordPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<DietRecord> queryWrapper = BuilderQuery(input);

        // 默认按记录时间排序
        queryWrapper = queryWrapper.orderByAsc(DietRecord::getRecordTime);

        // 查询所有符合条件的记录
        List<DietRecord> pageRecords = DietRecordMapper.selectList(queryWrapper);

        // 把DietRecord实体转换成DietRecord传输模型
        List<DietRecordDto> items = Extension.copyBeanList(pageRecords, DietRecordDto.class);

        // 处理外键关联数据
        DispatchItem(items);

        // 按日期分组汇总
        HashMap<String, List<DietRecordDto>> groupedByDate = new HashMap<>();

        for (DietRecordDto item : items) {
            if (item.getRecordTime() != null) {
                // 获取日期字符串 (yyyy-MM-dd格式)
                String dateStr = item.getRecordTime().toLocalDate().toString();

                if (!groupedByDate.containsKey(dateStr)) {
                    groupedByDate.put(dateStr, new ArrayList<>());
                }
                groupedByDate.get(dateStr).add(item);
            }
        }

        // 构建返回结果
        List<DietRecordByDaySummaryDto> result = new ArrayList<>();

        for (String dateStr : groupedByDate.keySet()) {
            List<DietRecordDto> dayRecords = groupedByDate.get(dateStr);

            DietRecordByDaySummaryDto summaryDto = new DietRecordByDaySummaryDto();
            summaryDto.setRecordDate(dateStr);

            // 计算当天的营养总量
            double totalCalories = 0.0;
            double totalProtein = 0.0;
            double totalCarbohydrates = 0.0;
            double totalFat = 0.0;
            int recordCount = dayRecords.size();

            for (DietRecordDto record : dayRecords) {
                if (record.getFoodUnitDto() != null) {
                    // 计算实际摄入量 = 单位营养值 * 摄入数量
                    double recordValue = record.getRecordValue() != null ? record.getRecordValue().doubleValue() : 0.0;

                    if (record.getFoodUnitDto().getCalories() != null) {
                        totalCalories += record.getFoodUnitDto().getCalories() * recordValue;
                    }
                    if (record.getFoodUnitDto().getProtein() != null) {
                        totalProtein += record.getFoodUnitDto().getProtein() * recordValue;
                    }
                    if (record.getFoodUnitDto().getCarbohydrates() != null) {
                        totalCarbohydrates += record.getFoodUnitDto().getCarbohydrates() * recordValue;
                    }
                    if (record.getFoodUnitDto().getFat() != null) {
                        totalFat += record.getFoodUnitDto().getFat() * recordValue;
                    }
                }
            }

            // 设置汇总数据，保留4位小数
            summaryDto.setTotalCalories(Extension.ToFixed4(totalCalories));
            summaryDto.setTotalProtein(Extension.ToFixed4(totalProtein));
            summaryDto.setTotalCarbohydrates(Extension.ToFixed4(totalCarbohydrates));
            summaryDto.setTotalFat(Extension.ToFixed4(totalFat));
            summaryDto.setRecordCount(recordCount);

            result.add(summaryDto);
        }

        // 按日期排序，最新的在前面
        result.sort((a, b) -> b.getRecordDate().compareTo(a.getRecordDate()));

        return result;
    }

    /**
     * 计算营养总和
     */
    private void calculateNutritionTotals(DietRecordByDayDto dayDto, List<DietRecordDto> records) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbohydrates = 0.0;
        double totalFat = 0.0;

        for (DietRecordDto record : records) {
            if (record.getFoodUnitDto() != null) {
                // 计算实际摄入量 = 单位营养值 * 摄入数量
                double recordValue = record.getRecordValue() != null ? record.getRecordValue().doubleValue() : 0.0;

                if (record.getFoodUnitDto().getCalories() != null) {
                    totalCalories += record.getFoodUnitDto().getCalories() * recordValue;
                }
                if (record.getFoodUnitDto().getProtein() != null) {
                    totalProtein += record.getFoodUnitDto().getProtein() * recordValue;
                }
                if (record.getFoodUnitDto().getCarbohydrates() != null) {
                    totalCarbohydrates += record.getFoodUnitDto().getCarbohydrates() * recordValue;
                }
                if (record.getFoodUnitDto().getFat() != null) {
                    totalFat += record.getFoodUnitDto().getFat() * recordValue;
                }
            }
        }

        // 保留4位小数
        dayDto.setTotalCalories(Extension.ToFixed4(totalCalories));
        dayDto.setTotalProtein(Extension.ToFixed4(totalProtein));
        dayDto.setTotalCarbohydrates(Extension.ToFixed4(totalCarbohydrates));
        dayDto.setTotalFat(Extension.ToFixed4(totalFat));
    }

}
