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
 * 健康指标分类功能实现类
 */
@Service
public class HealthIndicatorTypeServiceImpl extends ServiceImpl<HealthIndicatorTypeMapper, HealthIndicatorType> implements HealthIndicatorTypeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthIndicatorType表mapper对象
     */
    @Autowired
    private HealthIndicatorTypeMapper HealthIndicatorTypeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthIndicatorType> BuilderQuery(HealthIndicatorTypePagedInput input) {
       //声明一个支持健康指标分类查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthIndicatorType> queryWrapper = Wrappers.<HealthIndicatorType>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthIndicatorType::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getName())) {
             queryWrapper = queryWrapper.like(HealthIndicatorType::getName, input.getName());
       	 }

        if (input.getBelongUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicatorType::getBelongUserId, input.getBelongUserId());
       	 }
        if (input.getIsComm() != null) {
            queryWrapper = queryWrapper.eq(HealthIndicatorType::getIsComm, input.getIsComm());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HealthIndicatorType::getName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理健康指标分类对于的外键数据
     */
   private List<HealthIndicatorTypeDto> DispatchItem(List<HealthIndicatorTypeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HealthIndicatorTypeDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  BelongUserEntity= AppUserMapper.selectById(item.getBelongUserId());
            item.setBelongUserDto(BelongUserEntity!=null?BelongUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 健康指标分类分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthIndicatorTypeDto> List(HealthIndicatorTypePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<HealthIndicatorType> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthIndicatorType::getCreationTime);
        }

        //构建一个分页查询的model
        Page<HealthIndicatorType> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取健康指标分类数据
        IPage<HealthIndicatorType> pageRecords= HealthIndicatorTypeMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= HealthIndicatorTypeMapper.selectCount(queryWrapper);
        //把HealthIndicatorType实体转换成HealthIndicatorType传输模型
        List<HealthIndicatorTypeDto> items= Extension.copyBeanList(pageRecords.getRecords(),HealthIndicatorTypeDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个健康指标分类查询
     */
    @SneakyThrows
    @Override
    public HealthIndicatorTypeDto Get(HealthIndicatorTypePagedInput input) {
       if(input.getId()==null)
        {
         return new HealthIndicatorTypeDto();
        }
      
       PagedResult<HealthIndicatorTypeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthIndicatorTypeDto()); 
    }

    /**
     *健康指标分类创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthIndicatorTypeDto CreateOrEdit(HealthIndicatorTypeDto input) {
        //声明一个健康指标分类实体
        HealthIndicatorType HealthIndicatorType=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(HealthIndicatorType);
        //把传输模型返回给前端
        return HealthIndicatorType.MapToDto();
    }
    /**
     * 健康指标分类删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthIndicatorType entity = HealthIndicatorTypeMapper.selectById(input.getId());
        HealthIndicatorTypeMapper.deleteById(entity);
    }

    /**
     * 健康指标分类批量删除
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
