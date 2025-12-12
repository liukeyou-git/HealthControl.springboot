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
 * 运动单位功能实现类
 */
@Service
public class SportUnitServiceImpl extends ServiceImpl<SportUnitMapper, SportUnit> implements SportUnitService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的SportUnit表mapper对象
     */
    @Autowired
    private SportUnitMapper SportUnitMapper;
    @Autowired
    private SportMapper  SportMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<SportUnit> BuilderQuery(SportUnitPagedInput input) {
       //声明一个支持运动单位查询的(拉姆达)表达式
        LambdaQueryWrapper<SportUnit> queryWrapper = Wrappers.<SportUnit>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, SportUnit::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getUnitName())) {
             queryWrapper = queryWrapper.like(SportUnit::getUnitName, input.getUnitName());
       	 }

        if (input.getSportId() != null) {
            queryWrapper = queryWrapper.eq(SportUnit::getSportId, input.getSportId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(SportUnit::getUnitName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理运动单位对于的外键数据
     */
   private List<SportUnitDto> DispatchItem(List<SportUnitDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (SportUnitDto item : items) {           
          	            
           //查询出关联的Sport表信息           
            Sport  SportEntity= SportMapper.selectById(item.getSportId());
            item.setSportDto(SportEntity!=null?SportEntity.MapToDto():new SportDto());              
       }
       
     return items; 
   }
  
    /**
     * 运动单位分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<SportUnitDto> List(SportUnitPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<SportUnit> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(SportUnit::getCreationTime);
        }

        //构建一个分页查询的model
        Page<SportUnit> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取运动单位数据
        IPage<SportUnit> pageRecords= SportUnitMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= SportUnitMapper.selectCount(queryWrapper);
        //把SportUnit实体转换成SportUnit传输模型
        List<SportUnitDto> items= Extension.copyBeanList(pageRecords.getRecords(),SportUnitDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个运动单位查询
     */
    @SneakyThrows
    @Override
    public SportUnitDto Get(SportUnitPagedInput input) {
       if(input.getId()==null)
        {
         return new SportUnitDto();
        }
      
       PagedResult<SportUnitDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new SportUnitDto()); 
    }

    /**
     *运动单位创建或者修改
     */
    @SneakyThrows
    @Override
    public SportUnitDto CreateOrEdit(SportUnitDto input) {
        //声明一个运动单位实体
        SportUnit SportUnit=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(SportUnit);
        //把传输模型返回给前端
        return SportUnit.MapToDto();
    }
    /**
     * 运动单位删除
     */
    @Override
    public void Delete(IdInput input) {
        SportUnit entity = SportUnitMapper.selectById(input.getId());
        SportUnitMapper.deleteById(entity);
    }

    /**
     * 运动单位批量删除
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
