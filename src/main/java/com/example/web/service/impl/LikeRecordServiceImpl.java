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
 * 点赞记录功能实现类
 */
@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的LikeRecord表mapper对象
     */
    @Autowired
    private LikeRecordMapper LikeRecordMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<LikeRecord> BuilderQuery(LikeRecordPagedInput input) {
       //声明一个支持点赞记录查询的(拉姆达)表达式
        LambdaQueryWrapper<LikeRecord> queryWrapper = Wrappers.<LikeRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, LikeRecord::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getLikeType())) {
             queryWrapper = queryWrapper.like(LikeRecord::getLikeType, input.getLikeType());
       	 }
        if (Extension.isNotNullOrEmpty(input.getRelativeId())) {
             queryWrapper = queryWrapper.like(LikeRecord::getRelativeId, input.getRelativeId());
       	 }

        if (input.getLikeUserId() != null) {
            queryWrapper = queryWrapper.eq(LikeRecord::getLikeUserId, input.getLikeUserId());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(LikeRecord::getLikeType,input.getKeyWord()).or()   	 
          	   .like(LikeRecord::getRelativeId,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理点赞记录对于的外键数据
     */
   private List<LikeRecordDto> DispatchItem(List<LikeRecordDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (LikeRecordDto item : items) {           
          	            
           //查询出关联的AppUser表信息           
            AppUser  LikeUserEntity= AppUserMapper.selectById(item.getLikeUserId());
            item.setLikeUserDto(LikeUserEntity!=null?LikeUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 点赞记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<LikeRecordDto> List(LikeRecordPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<LikeRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(LikeRecord::getCreationTime);
        }

        //构建一个分页查询的model
        Page<LikeRecord> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取点赞记录数据
        IPage<LikeRecord> pageRecords= LikeRecordMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= LikeRecordMapper.selectCount(queryWrapper);
        //把LikeRecord实体转换成LikeRecord传输模型
        List<LikeRecordDto> items= Extension.copyBeanList(pageRecords.getRecords(),LikeRecordDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个点赞记录查询
     */
    @SneakyThrows
    @Override
    public LikeRecordDto Get(LikeRecordPagedInput input) {
       if(input.getId()==null)
        {
         return new LikeRecordDto();
        }
      
       PagedResult<LikeRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new LikeRecordDto()); 
    }

    /**
     *点赞记录创建或者修改
     */
    @SneakyThrows
    @Override
    public LikeRecordDto CreateOrEdit(LikeRecordDto input) {
        //声明一个点赞记录实体
        LikeRecord LikeRecord=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(LikeRecord);
        //把传输模型返回给前端
        return LikeRecord.MapToDto();
    }
    /**
     * 点赞记录删除
     */
    @Override
    public void Delete(IdInput input) {
        LikeRecord entity = LikeRecordMapper.selectById(input.getId());
        LikeRecordMapper.deleteById(entity);
    }

    /**
     * 点赞记录批量删除
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
