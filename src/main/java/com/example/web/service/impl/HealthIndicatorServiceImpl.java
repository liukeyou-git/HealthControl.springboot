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
 * 健康指标功能实现类
 */
@Service
public class HealthIndicatorServiceImpl extends ServiceImpl<HealthIndicatorMapper, HealthIndicator> implements HealthIndicatorService {

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
    private HealthIndicatorTypeMapper  HealthIndicatorTypeMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthIndicator> BuilderQuery(HealthIndicatorPagedInput input) {
       //声明一个支持健康指标查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthIndicator> queryWrapper = Wrappers.<HealthIndicator>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthIndicator::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
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
        if (input.getIsComm() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicator::getIsComm, input.getIsComm());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HealthIndicator::getName,input.getKeyWord()).or()   	 
          	   .like(HealthIndicator::getThreshold,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理健康指标对于的外键数据
     */
   private List<HealthIndicatorDto> DispatchItem(List<HealthIndicatorDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HealthIndicatorDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  BelongUserEntity= AppUserMapper.selectById(item.getBelongUserId());
            item.setBelongUserDto(BelongUserEntity!=null?BelongUserEntity.MapToDto():new AppUserDto());              
           
          	            
           //查询出关联的HealthIndicatorType表信息           
            HealthIndicatorType  HealthIndicatorTypeEntity= HealthIndicatorTypeMapper.selectById(item.getHealthIndicatorTypeId());
            item.setHealthIndicatorTypeDto(HealthIndicatorTypeEntity!=null?HealthIndicatorTypeEntity.MapToDto():new HealthIndicatorTypeDto());              
       }
       
     return items; 
   }
  
    /**
     * 健康指标分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthIndicatorDto> List(HealthIndicatorPagedInput input) {
			//构建where条件+排序
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

        //构建一个分页查询的model
        Page<HealthIndicator> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取健康指标数据
        IPage<HealthIndicator> pageRecords= HealthIndicatorMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= HealthIndicatorMapper.selectCount(queryWrapper);
        //把HealthIndicator实体转换成HealthIndicator传输模型
        List<HealthIndicatorDto> items= Extension.copyBeanList(pageRecords.getRecords(),HealthIndicatorDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个健康指标查询
     */
    @SneakyThrows
    @Override
    public HealthIndicatorDto Get(HealthIndicatorPagedInput input) {
       if(input.getId()==null)
        {
         return new HealthIndicatorDto();
        }
      
       PagedResult<HealthIndicatorDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthIndicatorDto()); 
    }

    /**
     *健康指标创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthIndicatorDto CreateOrEdit(HealthIndicatorDto input) {
        //声明一个健康指标实体
        HealthIndicator HealthIndicator=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(HealthIndicator);
        //把传输模型返回给前端
        return HealthIndicator.MapToDto();
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
