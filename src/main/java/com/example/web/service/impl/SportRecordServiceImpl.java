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
 * 运动记录功能实现类
 */
@Service
public class SportRecordServiceImpl extends ServiceImpl<SportRecordMapper, SportRecord> implements SportRecordService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的SportRecord表mapper对象
     */
    @Autowired
    private SportRecordMapper SportRecordMapper;
    @Autowired
    private SportMapper  SportMapper;                        
    @Autowired
    private SportUnitMapper  SportUnitMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<SportRecord> BuilderQuery(SportRecordPagedInput input) {
       //声明一个支持运动记录查询的(拉姆达)表达式
        LambdaQueryWrapper<SportRecord> queryWrapper = Wrappers.<SportRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, SportRecord::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件

        if (input.getSportId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getSportId, input.getSportId());
       	 }

        if (input.getSportUnitId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getSportUnitId, input.getSportUnitId());
       	 }

        if (input.getRecordUserId() != null) {
            queryWrapper = queryWrapper.eq(SportRecord::getRecordUserId, input.getRecordUserId());
       	 }
        if (input.getRecordTimeRange() != null && !input.getRecordTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(SportRecord::getRecordTime, input.getRecordTimeRange().get(1));
            queryWrapper = queryWrapper.ge(SportRecord::getRecordTime, input.getRecordTimeRange().get(0));
        }
      

 
    
      return queryWrapper;
    }
  
    /**
     * 处理运动记录对于的外键数据
     */
   private List<SportRecordDto> DispatchItem(List<SportRecordDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (SportRecordDto item : items) {           
          	            
           //查询出关联的Sport表信息           
            Sport  SportEntity= SportMapper.selectById(item.getSportId());
            item.setSportDto(SportEntity!=null?SportEntity.MapToDto():new SportDto());              
           
          	            
           //查询出关联的SportUnit表信息           
            SportUnit  SportUnitEntity= SportUnitMapper.selectById(item.getSportUnitId());
            item.setSportUnitDto(SportUnitEntity!=null?SportUnitEntity.MapToDto():new SportUnitDto());              
           
          	            
           //查询出关联的AppUser表信息           
            AppUser  RecordUserEntity= AppUserMapper.selectById(item.getRecordUserId());
            item.setRecordUserDto(RecordUserEntity!=null?RecordUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 运动记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<SportRecordDto> List(SportRecordPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<SportRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(SportRecord::getCreationTime);
        }

        //构建一个分页查询的model
        Page<SportRecord> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取运动记录数据
        IPage<SportRecord> pageRecords= SportRecordMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= SportRecordMapper.selectCount(queryWrapper);
        //把SportRecord实体转换成SportRecord传输模型
        List<SportRecordDto> items= Extension.copyBeanList(pageRecords.getRecords(),SportRecordDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个运动记录查询
     */
    @SneakyThrows
    @Override
    public SportRecordDto Get(SportRecordPagedInput input) {
       if(input.getId()==null)
        {
         return new SportRecordDto();
        }
      
       PagedResult<SportRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new SportRecordDto()); 
    }

    /**
     *运动记录创建或者修改
     */
    @SneakyThrows
    @Override
    public SportRecordDto CreateOrEdit(SportRecordDto input) {
        //声明一个运动记录实体
        SportRecord SportRecord=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(SportRecord);
        //把传输模型返回给前端
        return SportRecord.MapToDto();
    }
    /**
     * 运动记录删除
     */
    @Override
    public void Delete(IdInput input) {
        SportRecord entity = SportRecordMapper.selectById(input.getId());
        SportRecordMapper.deleteById(entity);
    }

    /**
     * 运动记录批量删除
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
