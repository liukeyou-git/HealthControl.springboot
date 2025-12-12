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
 * 食物功能实现类
 */
@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的Food表mapper对象
     */
    @Autowired
    private FoodMapper FoodMapper;
    @Autowired
    private FoodTypeMapper  FoodTypeMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Food> BuilderQuery(FoodPagedInput input) {
       //声明一个支持食物查询的(拉姆达)表达式
        LambdaQueryWrapper<Food> queryWrapper = Wrappers.<Food>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Food::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getName())) {
             queryWrapper = queryWrapper.like(Food::getName, input.getName());
       	 }

        if (input.getFoodTypeId() != null) {
            queryWrapper = queryWrapper.eq(Food::getFoodTypeId, input.getFoodTypeId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(Food::getName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理食物对于的外键数据
     */
   private List<FoodDto> DispatchItem(List<FoodDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (FoodDto item : items) {           
          	            
           //查询出关联的FoodType表信息           
            FoodType  FoodTypeEntity= FoodTypeMapper.selectById(item.getFoodTypeId());
            item.setFoodTypeDto(FoodTypeEntity!=null?FoodTypeEntity.MapToDto():new FoodTypeDto());              
       }
       
     return items; 
   }
  
    /**
     * 食物分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<FoodDto> List(FoodPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<Food> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Food::getCreationTime);
        }

        //构建一个分页查询的model
        Page<Food> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取食物数据
        IPage<Food> pageRecords= FoodMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= FoodMapper.selectCount(queryWrapper);
        //把Food实体转换成Food传输模型
        List<FoodDto> items= Extension.copyBeanList(pageRecords.getRecords(),FoodDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个食物查询
     */
    @SneakyThrows
    @Override
    public FoodDto Get(FoodPagedInput input) {
       if(input.getId()==null)
        {
         return new FoodDto();
        }
      
       PagedResult<FoodDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new FoodDto()); 
    }

    /**
     *食物创建或者修改
     */
    @SneakyThrows
    @Override
    public FoodDto CreateOrEdit(FoodDto input) {
        //声明一个食物实体
        Food Food=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(Food);
        //把传输模型返回给前端
        return Food.MapToDto();
    }
    /**
     * 食物删除
     */
    @Override
    public void Delete(IdInput input) {
        Food entity = FoodMapper.selectById(input.getId());
        FoodMapper.deleteById(entity);
    }

    /**
     * 食物批量删除
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
