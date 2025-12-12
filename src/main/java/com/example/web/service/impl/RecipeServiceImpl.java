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
 * 食谱功能实现类
 */
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的Recipe表mapper对象
     */
    @Autowired
    private RecipeMapper RecipeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Recipe> BuilderQuery(RecipePagedInput input) {
       //声明一个支持食谱查询的(拉姆达)表达式
        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Recipe::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
             queryWrapper = queryWrapper.like(Recipe::getTitle, input.getTitle());
       	 }

        if (input.getAuditUserId() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getAuditUserId, input.getAuditUserId());
       	 }

        if (input.getPublishUserId() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getPublishUserId, input.getPublishUserId());
       	 }

        if (input.getAuditStatus() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getAuditStatus, input.getAuditStatus());
       	 }
        if (input.getAuditTimeRange() != null && !input.getAuditTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(Recipe::getAuditTime, input.getAuditTimeRange().get(1));
            queryWrapper = queryWrapper.ge(Recipe::getAuditTime, input.getAuditTimeRange().get(0));
        }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(Recipe::getTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理食谱对于的外键数据
     */
   private List<RecipeDto> DispatchItem(List<RecipeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (RecipeDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  AuditUserEntity= AppUserMapper.selectById(item.getAuditUserId());
            item.setAuditUserDto(AuditUserEntity!=null?AuditUserEntity.MapToDto():new AppUserDto());              
           
          	            
           //查询出关联的AppUser表信息           
            AppUser  PublishUserEntity= AppUserMapper.selectById(item.getPublishUserId());
            item.setPublishUserDto(PublishUserEntity!=null?PublishUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 食谱分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<RecipeDto> List(RecipePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<Recipe> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Recipe::getCreationTime);
        }

        //构建一个分页查询的model
        Page<Recipe> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取食谱数据
        IPage<Recipe> pageRecords= RecipeMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= RecipeMapper.selectCount(queryWrapper);
        //把Recipe实体转换成Recipe传输模型
        List<RecipeDto> items= Extension.copyBeanList(pageRecords.getRecords(),RecipeDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个食谱查询
     */
    @SneakyThrows
    @Override
    public RecipeDto Get(RecipePagedInput input) {
       if(input.getId()==null)
        {
         return new RecipeDto();
        }
      
       PagedResult<RecipeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new RecipeDto()); 
    }

    /**
     *食谱创建或者修改
     */
    @SneakyThrows
    @Override
    public RecipeDto CreateOrEdit(RecipeDto input) {
        //声明一个食谱实体
        Recipe Recipe=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(Recipe);
        //把传输模型返回给前端
        return Recipe.MapToDto();
    }
    /**
     * 食谱删除
     */
    @Override
    public void Delete(IdInput input) {
        Recipe entity = RecipeMapper.selectById(input.getId());
        RecipeMapper.deleteById(entity);
    }

    /**
     * 食谱批量删除
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
