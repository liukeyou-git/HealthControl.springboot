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
 * 健康知识分类功能实现类
 */
@Service
public class HealthArticleTypeServiceImpl extends ServiceImpl<HealthArticleTypeMapper, HealthArticleType> implements HealthArticleTypeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthArticleType表mapper对象
     */
    @Autowired
    private HealthArticleTypeMapper HealthArticleTypeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthArticleType> BuilderQuery(HealthArticleTypePagedInput input) {
       //声明一个支持健康知识分类查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthArticleType> queryWrapper = Wrappers.<HealthArticleType>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthArticleType::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getName())) {
             queryWrapper = queryWrapper.like(HealthArticleType::getName, input.getName());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HealthArticleType::getName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理健康知识分类对于的外键数据
     */
   private List<HealthArticleTypeDto> DispatchItem(List<HealthArticleTypeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HealthArticleTypeDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 健康知识分类分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthArticleTypeDto> List(HealthArticleTypePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<HealthArticleType> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthArticleType::getCreationTime);
        }

        //构建一个分页查询的model
        Page<HealthArticleType> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取健康知识分类数据
        IPage<HealthArticleType> pageRecords= HealthArticleTypeMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= HealthArticleTypeMapper.selectCount(queryWrapper);
        //把HealthArticleType实体转换成HealthArticleType传输模型
        List<HealthArticleTypeDto> items= Extension.copyBeanList(pageRecords.getRecords(),HealthArticleTypeDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个健康知识分类查询
     */
    @SneakyThrows
    @Override
    public HealthArticleTypeDto Get(HealthArticleTypePagedInput input) {
       if(input.getId()==null)
        {
         return new HealthArticleTypeDto();
        }
      
       PagedResult<HealthArticleTypeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthArticleTypeDto()); 
    }

    /**
     *健康知识分类创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthArticleTypeDto CreateOrEdit(HealthArticleTypeDto input) {
        //声明一个健康知识分类实体
        HealthArticleType HealthArticleType=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(HealthArticleType);
        //把传输模型返回给前端
        return HealthArticleType.MapToDto();
    }
    /**
     * 健康知识分类删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthArticleType entity = HealthArticleTypeMapper.selectById(input.getId());
        HealthArticleTypeMapper.deleteById(entity);
    }

    /**
     * 健康知识分类批量删除
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
