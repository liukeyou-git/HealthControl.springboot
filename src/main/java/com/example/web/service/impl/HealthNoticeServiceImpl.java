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
 * 健康提醒功能实现类
 */
@Service
public class HealthNoticeServiceImpl extends ServiceImpl<HealthNoticeMapper, HealthNotice> implements HealthNoticeService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthNotice表mapper对象
     */
    @Autowired
    private HealthNoticeMapper HealthNoticeMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthNotice> BuilderQuery(HealthNoticePagedInput input) {
       //声明一个支持健康提醒查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthNotice> queryWrapper = Wrappers.<HealthNotice>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthNotice::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getContent())) {
             queryWrapper = queryWrapper.like(HealthNotice::getContent, input.getContent());
       	 }
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
             queryWrapper = queryWrapper.like(HealthNotice::getTitle, input.getTitle());
       	 }
        if (Extension.isNotNullOrEmpty(input.getRemindType())) {
             queryWrapper = queryWrapper.like(HealthNotice::getRemindType, input.getRemindType());
       	 }

        if (input.getPublishUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthNotice::getPublishUserId, input.getPublishUserId());
       	 }
        if (input.getRemindTimeRange() != null && !input.getRemindTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(HealthNotice::getRemindTime, input.getRemindTimeRange().get(1));
            queryWrapper = queryWrapper.ge(HealthNotice::getRemindTime, input.getRemindTimeRange().get(0));
        }
        if (input.getIsRemind() != null) {
            queryWrapper = queryWrapper.eq(HealthNotice::getIsRemind, input.getIsRemind());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(HealthNotice::getContent,input.getKeyWord()).or()   	 
          	   .like(HealthNotice::getTitle,input.getKeyWord()).or()   	 
          	   .like(HealthNotice::getRemindType,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理健康提醒对于的外键数据
     */
   private List<HealthNoticeDto> DispatchItem(List<HealthNoticeDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (HealthNoticeDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  PublishUserEntity= AppUserMapper.selectById(item.getPublishUserId());
            item.setPublishUserDto(PublishUserEntity!=null?PublishUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 健康提醒分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthNoticeDto> List(HealthNoticePagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<HealthNotice> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthNotice::getCreationTime);
        }

        //构建一个分页查询的model
        Page<HealthNotice> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取健康提醒数据
        IPage<HealthNotice> pageRecords= HealthNoticeMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= HealthNoticeMapper.selectCount(queryWrapper);
        //把HealthNotice实体转换成HealthNotice传输模型
        List<HealthNoticeDto> items= Extension.copyBeanList(pageRecords.getRecords(),HealthNoticeDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个健康提醒查询
     */
    @SneakyThrows
    @Override
    public HealthNoticeDto Get(HealthNoticePagedInput input) {
       if(input.getId()==null)
        {
         return new HealthNoticeDto();
        }
      
       PagedResult<HealthNoticeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthNoticeDto()); 
    }

    /**
     *健康提醒创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthNoticeDto CreateOrEdit(HealthNoticeDto input) {
        //声明一个健康提醒实体
        HealthNotice HealthNotice=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(HealthNotice);
        //把传输模型返回给前端
        return HealthNotice.MapToDto();
    }
    /**
     * 健康提醒删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthNotice entity = HealthNoticeMapper.selectById(input.getId());
        HealthNoticeMapper.deleteById(entity);
    }

    /**
     * 健康提醒批量删除
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
