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
 * 食物单位功能实现类
 */
@Service
public class FoodUnitServiceImpl extends ServiceImpl<FoodUnitMapper, FoodUnit> implements FoodUnitService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的FoodUnit表mapper对象
     */
    @Autowired
    private FoodUnitMapper FoodUnitMapper;
    @Autowired
    private FoodMapper  FoodMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<FoodUnit> BuilderQuery(FoodUnitPagedInput input) {
       //声明一个支持食物单位查询的(拉姆达)表达式
        LambdaQueryWrapper<FoodUnit> queryWrapper = Wrappers.<FoodUnit>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, FoodUnit::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getUnitName())) {
             queryWrapper = queryWrapper.like(FoodUnit::getUnitName, input.getUnitName());
       	 }

        if (input.getFoodId() != null) {
            queryWrapper = queryWrapper.eq(FoodUnit::getFoodId, input.getFoodId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(FoodUnit::getUnitName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理食物单位对于的外键数据
     */
   private List<FoodUnitDto> DispatchItem(List<FoodUnitDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (FoodUnitDto item : items) {           
          	            
           //查询出关联的Food表信息           
            Food  FoodEntity= FoodMapper.selectById(item.getFoodId());
            item.setFoodDto(FoodEntity!=null?FoodEntity.MapToDto():new FoodDto());              
       }
       
     return items; 
   }
  
    /**
     * 食物单位分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<FoodUnitDto> List(FoodUnitPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<FoodUnit> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(FoodUnit::getCreationTime);
        }

        //构建一个分页查询的model
        Page<FoodUnit> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取食物单位数据
        IPage<FoodUnit> pageRecords= FoodUnitMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= FoodUnitMapper.selectCount(queryWrapper);
        //把FoodUnit实体转换成FoodUnit传输模型
        List<FoodUnitDto> items= Extension.copyBeanList(pageRecords.getRecords(),FoodUnitDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个食物单位查询
     */
    @SneakyThrows
    @Override
    public FoodUnitDto Get(FoodUnitPagedInput input) {
       if(input.getId()==null)
        {
         return new FoodUnitDto();
        }
      
       PagedResult<FoodUnitDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new FoodUnitDto()); 
    }

    /**
     *食物单位创建或者修改
     */
    @SneakyThrows
    @Override
    public FoodUnitDto CreateOrEdit(FoodUnitDto input) {
        //声明一个食物单位实体
        FoodUnit FoodUnit=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(FoodUnit);
        //把传输模型返回给前端
        return FoodUnit.MapToDto();
    }
    /**
     * 食物单位删除
     */
    @Override
    public void Delete(IdInput input) {
        FoodUnit entity = FoodUnitMapper.selectById(input.getId());
        FoodUnitMapper.deleteById(entity);
    }

    /**
     * 食物单位批量删除
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
