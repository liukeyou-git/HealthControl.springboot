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
 * 健康知识功能实现类
 */
@Service
public class HealthArticleServiceImpl extends ServiceImpl<HealthArticleMapper, HealthArticle> implements HealthArticleService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthArticle表mapper对象
     */
    @Autowired
    private HealthArticleMapper HealthArticleMapper;
    @Autowired
    private HealthArticleTypeMapper  HealthArticleTypeMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthArticle> BuilderQuery(HealthArticlePagedInput input) {
       //声明一个支持健康知识查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthArticle::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
             queryWrapper = queryWrapper.like(HealthArticle::getTitle, input.getTitle());
       	 }

        if (input.getHealthArticleTypeId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getHealthArticleTypeId, input.getHealthArticleTypeId());
       	 }

        if (input.getPublishUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getPublishUserId, input.getPublishUserId());
       	 }

        if (input.getAuditStatus() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getAuditStatus, input.getAuditStatus());
       	 }

        if (input.getAuditUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getAuditUserId, input.getAuditUserId());
       	 }
        if (input.getAuditTimeRange() != null && !input.getAuditTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(HealthArticle::getAuditTime, input.getAuditTimeRange().get(1));
            queryWrapper = queryWrapper.ge(HealthArticle::getAuditTime, input.getAuditTimeRange().get(0));
        }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HealthArticle::getTitle,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理健康知识对于的外键数据
     */
   private List<HealthArticleDto> DispatchItem(List<HealthArticleDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HealthArticleDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  PublishUserEntity= AppUserMapper.selectById(item.getPublishUserId());
            item.setPublishUserDto(PublishUserEntity!=null?PublishUserEntity.MapToDto():new AppUserDto());              
           
          	            
           //查询出关联的AppUser表信息           
            AppUser  AuditUserEntity= AppUserMapper.selectById(item.getAuditUserId());
            item.setAuditUserDto(AuditUserEntity!=null?AuditUserEntity.MapToDto():new AppUserDto());              
           
          	            
           //查询出关联的HealthArticleType表信息           
            HealthArticleType  HealthArticleTypeEntity= HealthArticleTypeMapper.selectById(item.getHealthArticleTypeId());
            item.setHealthArticleTypeDto(HealthArticleTypeEntity!=null?HealthArticleTypeEntity.MapToDto():new HealthArticleTypeDto());              
       }
       
     return items; 
   }
  
    /**
     * 健康知识分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthArticleDto> List(HealthArticlePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<HealthArticle> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthArticle::getCreationTime);
        }

        //构建一个分页查询的model
        Page<HealthArticle> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取健康知识数据
        IPage<HealthArticle> pageRecords= HealthArticleMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= HealthArticleMapper.selectCount(queryWrapper);
        //把HealthArticle实体转换成HealthArticle传输模型
        List<HealthArticleDto> items= Extension.copyBeanList(pageRecords.getRecords(),HealthArticleDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个健康知识查询
     */
    @SneakyThrows
    @Override
    public HealthArticleDto Get(HealthArticlePagedInput input) {
       if(input.getId()==null)
        {
         return new HealthArticleDto();
        }
      
       PagedResult<HealthArticleDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthArticleDto()); 
    }

    /**
     *健康知识创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthArticleDto CreateOrEdit(HealthArticleDto input) {
        //声明一个健康知识实体
        HealthArticle HealthArticle=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(HealthArticle);
        //把传输模型返回给前端
        return HealthArticle.MapToDto();
    }
    /**
     * 健康知识删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthArticle entity = HealthArticleMapper.selectById(input.getId());
        HealthArticleMapper.deleteById(entity);
    }

    /**
     * 健康知识批量删除
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
