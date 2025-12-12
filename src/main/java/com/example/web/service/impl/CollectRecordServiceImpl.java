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
 * 收藏记录功能实现类
 */
@Service
public class CollectRecordServiceImpl extends ServiceImpl<CollectRecordMapper, CollectRecord> implements CollectRecordService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的CollectRecord表mapper对象
     */
    @Autowired
    private CollectRecordMapper CollectRecordMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<CollectRecord> BuilderQuery(CollectRecordPagedInput input) {
       //声明一个支持收藏记录查询的(拉姆达)表达式
        LambdaQueryWrapper<CollectRecord> queryWrapper = Wrappers.<CollectRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, CollectRecord::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getCollectType())) {
             queryWrapper = queryWrapper.like(CollectRecord::getCollectType, input.getCollectType());
       	 }

        if (input.getCollectUserId() != null) {
            queryWrapper = queryWrapper.eq(CollectRecord::getCollectUserId, input.getCollectUserId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(CollectRecord::getCollectType,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理收藏记录对于的外键数据
     */
   private List<CollectRecordDto> DispatchItem(List<CollectRecordDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (CollectRecordDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  CollectUserEntity= AppUserMapper.selectById(item.getCollectUserId());
            item.setCollectUserDto(CollectUserEntity!=null?CollectUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 收藏记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<CollectRecordDto> List(CollectRecordPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<CollectRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(CollectRecord::getCreationTime);
        }

        //构建一个分页查询的model
        Page<CollectRecord> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取收藏记录数据
        IPage<CollectRecord> pageRecords= CollectRecordMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= CollectRecordMapper.selectCount(queryWrapper);
        //把CollectRecord实体转换成CollectRecord传输模型
        List<CollectRecordDto> items= Extension.copyBeanList(pageRecords.getRecords(),CollectRecordDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个收藏记录查询
     */
    @SneakyThrows
    @Override
    public CollectRecordDto Get(CollectRecordPagedInput input) {
       if(input.getId()==null)
        {
         return new CollectRecordDto();
        }
      
       PagedResult<CollectRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new CollectRecordDto()); 
    }

    /**
     *收藏记录创建或者修改
     */
    @SneakyThrows
    @Override
    public CollectRecordDto CreateOrEdit(CollectRecordDto input) {
        //声明一个收藏记录实体
        CollectRecord CollectRecord=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(CollectRecord);
        //把传输模型返回给前端
        return CollectRecord.MapToDto();
    }
    /**
     * 收藏记录删除
     */
    @Override
    public void Delete(IdInput input) {
        CollectRecord entity = CollectRecordMapper.selectById(input.getId());
        CollectRecordMapper.deleteById(entity);
    }

    /**
     * 收藏记录批量删除
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
