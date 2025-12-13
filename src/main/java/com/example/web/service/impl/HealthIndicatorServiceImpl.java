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
import com.example.web.tools.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import lombok.SneakyThrows;
import com.example.web.tools.*;

/**
 * 健康指标功能实现类
 */
@Service
public class HealthIndicatorServiceImpl extends ServiceImpl<HealthIndicatorMapper, HealthIndicator>
        implements HealthIndicatorService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthIndicator表mapper对象
     */
    @Autowired
    private HealthIndicatorMapper HealthIndicatorMapper;
    @Autowired
    private HealthIndicatorTypeMapper HealthIndicatorTypeMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthIndicator> BuilderQuery(HealthIndicatorPagedInput input) {
        // 声明一个支持健康指标查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthIndicator> queryWrapper = Wrappers.<HealthIndicator>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthIndicator::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getName())) {
            queryWrapper = queryWrapper.like(HealthIndicator::getName, input.getName());
        }
        if (Extension.isNotNullOrEmpty(input.getThreshold())) {
            queryWrapper = queryWrapper.like(HealthIndicator::getThreshold, input.getThreshold());
        }

        if (input.getBelongUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicator::getBelongUserId, input.getBelongUserId());
        }

        if (input.getHealthIndicatorTypeId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicator::getHealthIndicatorTypeId, input.getHealthIndicatorTypeId());
        }
        if (Extension.isNotNullOrEmpty(input.getContent())) {
            queryWrapper = queryWrapper.like(HealthIndicator::getContent, input.getContent());
        }
        if (input.getIsComm() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicator::getIsComm, input.getIsComm());
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(HealthIndicator::getName, input.getKeyWord()).or()
                    .like(HealthIndicator::getThreshold, input.getKeyWord()).or()
                    .like(HealthIndicator::getContent, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理健康指标对于的外键数据
     */
    private List<HealthIndicatorDto> DispatchItem(List<HealthIndicatorDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (HealthIndicatorDto item : items) {

            // 查询出关联的HealthIndicatorType表信息
            HealthIndicatorType HealthIndicatorTypeEntity = HealthIndicatorTypeMapper
                    .selectById(item.getHealthIndicatorTypeId());
            item.setHealthIndicatorTypeDto(HealthIndicatorTypeEntity != null ? HealthIndicatorTypeEntity.MapToDto()
                    : new HealthIndicatorTypeDto());

            // 查询出关联的AppUser表信息
            AppUser BelongUserEntity = AppUserMapper.selectById(item.getBelongUserId());
            item.setBelongUserDto(BelongUserEntity != null ? BelongUserEntity.MapToDto() : new AppUserDto());

        }

        return items;
    }

    /**
     * 健康指标分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthIndicatorDto> List(HealthIndicatorPagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<HealthIndicator> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthIndicator::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<HealthIndicator> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取健康指标数据
        IPage<HealthIndicator> pageRecords = HealthIndicatorMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = HealthIndicatorMapper.selectCount(queryWrapper);
        // 把HealthIndicator实体转换成HealthIndicator传输模型
        List<HealthIndicatorDto> items = Extension.copyBeanList(pageRecords.getRecords(), HealthIndicatorDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个健康指标查询
     */
    @SneakyThrows
    @Override
    public HealthIndicatorDto Get(HealthIndicatorPagedInput input) {
        if (input.getId() == null) {
            return new HealthIndicatorDto();
        }

        PagedResult<HealthIndicatorDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthIndicatorDto());
    }

    /**
     * 健康指标创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthIndicatorDto CreateOrEdit(HealthIndicatorDto input) {
        // 声明一个健康指标实体
        HealthIndicator HealthIndicator = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(HealthIndicator);
        // 把传输模型返回给前端
        return HealthIndicator.MapToDto();
    }

    /**
     * 用户添加公共指标
     */
    @SneakyThrows
    @Override
    public void UserAddCommIndicator(HealthIndicatorDto input) {
        input.setIsComm(false);
        // 判断该用户是否存在这个指标
        Long count = HealthIndicatorMapper.selectCount(
                Wrappers.<HealthIndicator>lambdaQuery()
                        .eq(HealthIndicator::getBelongUserId, input.getBelongUserId())
                        .eq(HealthIndicator::getName, input.getName()));

        if (count > 0) {
            throw new CustomException("该用户下面已存在此指标");
        }

        // 查询公共指标对应的类型
        HealthIndicatorType healthIndicatorType = HealthIndicatorTypeMapper
                .selectById(input.getHealthIndicatorTypeId());

        // 查询用户是否存在了这个类型的名称
        HealthIndicatorType userHealthIndicatorType = HealthIndicatorTypeMapper.selectList(
                        Wrappers.<HealthIndicatorType>lambdaQuery()
                                .eq(HealthIndicatorType::getBelongUserId, input.getBelongUserId())
                                .eq(HealthIndicatorType::getName, healthIndicatorType.getName()))
                .stream().findFirst().orElse(null);

        // 如果不存在我就插入这个类型
        if (userHealthIndicatorType == null) {
            HealthIndicatorType addHealthIndicatorType = new HealthIndicatorType();
            addHealthIndicatorType.setName(healthIndicatorType.getName());
            addHealthIndicatorType.setBelongUserId(input.getBelongUserId());
            addHealthIndicatorType.setIsComm(false);

            HealthIndicatorTypeMapper.insert(addHealthIndicatorType);
            input.setHealthIndicatorTypeId(addHealthIndicatorType.getId());
        } else {
            input.setHealthIndicatorTypeId(userHealthIndicatorType.getId());
        }

        // 声明一个健康指标实体
        HealthIndicator HealthIndicator = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(HealthIndicator);

    }

    /**
     * 用户取消
     */
    @SneakyThrows
    @Override
    public void UserCancelCommIndicator(HealthIndicatorDto input) {

        // 判断该用户是否存在这个指标
        HealthIndicator userHealthIndicator = HealthIndicatorMapper.selectList(
                        Wrappers.<HealthIndicator>lambdaQuery()
                                .eq(HealthIndicator::getBelongUserId, input.getBelongUserId())
                                .eq(HealthIndicator::getName, input.getName()))
                .stream().findFirst().orElse(null);
        if (userHealthIndicator != null) {
            // 得到当前这个指标分类下有多少个指标
            Long count = HealthIndicatorMapper.selectCount(
                    Wrappers.<HealthIndicator>lambdaQuery()
                            .eq(HealthIndicator::getHealthIndicatorTypeId,
                                    userHealthIndicator.getHealthIndicatorTypeId()));
            if (count == 1) {
                // 删除这个指标分类
                HealthIndicatorTypeMapper.deleteById(userHealthIndicator.getHealthIndicatorTypeId());
            }

            // 删除这个指标
            HealthIndicatorMapper.deleteById(userHealthIndicator);

        }

    }

    /**
     * 移除指标
     */
    @SneakyThrows
    @Override
    public void UserRemoveIndicator(HealthIndicatorDto input) {

        HealthIndicator entity = HealthIndicatorMapper.selectById(input.getId());

        if (entity != null) {
            // 得到当前这个指标分类下有多少个指标
            Long count = HealthIndicatorMapper.selectCount(
                    Wrappers.<HealthIndicator>lambdaQuery()
                            .eq(HealthIndicator::getHealthIndicatorTypeId,
                                    entity.getHealthIndicatorTypeId()));
            if (count == 1) {
                // 删除这个指标分类
                HealthIndicatorTypeMapper.deleteById(entity.getHealthIndicatorTypeId());
            }

            // 删除这个指标
            HealthIndicatorMapper.deleteById(entity);
        }
    }

    /**
     * 用户创建或则编辑指标
     */
    @SneakyThrows
    @Override
    public void UserCreateOrEditIndicator(HealthIndicatorDto input) {

        if (input.IsAdd()) {
            HealthIndicator entity = input.MapToEntity();
            entity.setBelongUserId(input.getBelongUserId());
            entity.setIsComm(false);
            entity.setContent(input.getContent());
            entity.setThreshold(input.getThreshold());
            entity.setName(input.getName());
            entity.setCover(input.getCover());

            HealthIndicatorType userHealthIndicatorType = HealthIndicatorTypeMapper.selectList(
                            Wrappers.<HealthIndicatorType>lambdaQuery()
                                    .eq(HealthIndicatorType::getBelongUserId, input.getBelongUserId())
                                    .eq(HealthIndicatorType::getName, input.getHealthIndicatorTypeName()))
                    .stream().findFirst().orElse(null);
            if (userHealthIndicatorType == null) {
                // 插入新的指标归类
                HealthIndicatorType addHealthIndicatorType = new HealthIndicatorType();
                addHealthIndicatorType.setName(input.getHealthIndicatorTypeName());
                addHealthIndicatorType.setBelongUserId(input.getBelongUserId());
                addHealthIndicatorType.setIsComm(false);
                HealthIndicatorTypeMapper.insert(addHealthIndicatorType);
                entity.setHealthIndicatorTypeId(addHealthIndicatorType.getId());
            } else {
                entity.setHealthIndicatorTypeId(userHealthIndicatorType.getId());
            }
            saveOrUpdate(entity);

        } else {
            // 查询当前的指标
            HealthIndicator entity = HealthIndicatorMapper.selectById(input.getId());

            // 查询当前的指标归类
            HealthIndicatorType healthIndicatorType = HealthIndicatorTypeMapper
                    .selectById(entity.getHealthIndicatorTypeId());

            // 判断本次是否修改了指标归类
            if (!input.getHealthIndicatorTypeName().equals(healthIndicatorType.getName())) {
                // 如果修改了指标，则需要判断当前的指标归类下有多少个指标
                Long count = HealthIndicatorMapper.selectCount(
                        Wrappers.<HealthIndicator>lambdaQuery()
                                .eq(HealthIndicator::getHealthIndicatorTypeId, entity.getHealthIndicatorTypeId()));
                if (count == 1) {
                    // 删除这个指标归类
                    HealthIndicatorTypeMapper.deleteById(entity.getHealthIndicatorTypeId());
                }
                // 然后查询自己名下是否有新的指标数据
                HealthIndicatorType userHealthIndicatorType = HealthIndicatorTypeMapper.selectList(
                                Wrappers.<HealthIndicatorType>lambdaQuery()
                                        .eq(HealthIndicatorType::getBelongUserId, input.getBelongUserId())
                                        .eq(HealthIndicatorType::getName, input.getHealthIndicatorTypeName()))
                        .stream().findFirst().orElse(null);
                if (userHealthIndicatorType == null) {
                    // 插入新的指标归类
                    HealthIndicatorType addHealthIndicatorType = new HealthIndicatorType();
                    addHealthIndicatorType.setName(input.getHealthIndicatorTypeName());
                    addHealthIndicatorType.setBelongUserId(input.getBelongUserId());
                    addHealthIndicatorType.setIsComm(false);
                    HealthIndicatorTypeMapper.insert(addHealthIndicatorType);
                    entity.setHealthIndicatorTypeId(addHealthIndicatorType.getId());
                } else {
                    entity.setHealthIndicatorTypeId(userHealthIndicatorType.getId());
                }
            }

            entity.setIsComm(false);
            entity.setContent(input.getContent());
            entity.setThreshold(input.getThreshold());
            entity.setName(input.getName());
            entity.setCover(input.getCover());

            saveOrUpdate(entity);
        }

    }

    /**
     * 健康指标删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthIndicator entity = HealthIndicatorMapper.selectById(input.getId());
        HealthIndicatorMapper.deleteById(entity);
    }

    /**
     * 健康指标批量删除
     */
    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            Delete(idInput);
        }
    }
}
