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
 * 食物类型功能实现类
 */
@Service
public class FoodTypeServiceImpl extends ServiceImpl<FoodTypeMapper, FoodType> implements FoodTypeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的FoodType表mapper对象
     */
    @Autowired
    private FoodTypeMapper FoodTypeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<FoodType> BuilderQuery(FoodTypePagedInput input) {
       //声明一个支持食物类型查询的(拉姆达)表达式
        LambdaQueryWrapper<FoodType> queryWrapper = Wrappers.<FoodType>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, FoodType::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getName())) {
             queryWrapper = queryWrapper.like(FoodType::getName, input.getName());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(FoodType::getName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理食物类型对于的外键数据
     */
   private List<FoodTypeDto> DispatchItem(List<FoodTypeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (FoodTypeDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 食物类型分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<FoodTypeDto> List(FoodTypePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<FoodType> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(FoodType::getCreationTime);
        }

        //构建一个分页查询的model
        Page<FoodType> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取食物类型数据
        IPage<FoodType> pageRecords= FoodTypeMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= FoodTypeMapper.selectCount(queryWrapper);
        //把FoodType实体转换成FoodType传输模型
        List<FoodTypeDto> items= Extension.copyBeanList(pageRecords.getRecords(),FoodTypeDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个食物类型查询
     */
    @SneakyThrows
    @Override
    public FoodTypeDto Get(FoodTypePagedInput input) {
       if(input.getId()==null)
        {
         return new FoodTypeDto();
        }
      
       PagedResult<FoodTypeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new FoodTypeDto()); 
    }

    /**
     *食物类型创建或者修改
     */
    @SneakyThrows
    @Override
    public FoodTypeDto CreateOrEdit(FoodTypeDto input) {
        //声明一个食物类型实体
        FoodType FoodType=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(FoodType);
        //把传输模型返回给前端
        return FoodType.MapToDto();
    }
    /**
     * 食物类型删除
     */
    @Override
    public void Delete(IdInput input) {
        FoodType entity = FoodTypeMapper.selectById(input.getId());
        FoodTypeMapper.deleteById(entity);
    }

    /**
     * 食物类型批量删除
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
