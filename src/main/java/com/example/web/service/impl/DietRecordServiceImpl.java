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
 * 饮食记录功能实现类
 */
@Service
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的DietRecord表mapper对象
     */
    @Autowired
    private DietRecordMapper DietRecordMapper;
    @Autowired
    private FoodMapper  FoodMapper;                        
    @Autowired
    private FoodUnitMapper  FoodUnitMapper;                        

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<DietRecord> BuilderQuery(DietRecordPagedInput input) {
       //声明一个支持饮食记录查询的(拉姆达)表达式
        LambdaQueryWrapper<DietRecord> queryWrapper = Wrappers.<DietRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, DietRecord::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件

        if (input.getFoodId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getFoodId, input.getFoodId());
       	 }

        if (input.getRecordUserId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getRecordUserId, input.getRecordUserId());
       	 }

        if (input.getFoodUnitId() != null) {
            queryWrapper = queryWrapper.eq(DietRecord::getFoodUnitId, input.getFoodUnitId());
       	 }
        if (input.getRecordTimeRange() != null && !input.getRecordTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(DietRecord::getRecordTime, input.getRecordTimeRange().get(1));
            queryWrapper = queryWrapper.ge(DietRecord::getRecordTime, input.getRecordTimeRange().get(0));
        }
      

 
    
      return queryWrapper;
    }
  
    /**
     * 处理饮食记录对于的外键数据
     */
   private List<DietRecordDto> DispatchItem(List<DietRecordDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (DietRecordDto item : items) {           
          	            
           //查询出关联的Food表信息           
            Food  FoodEntity= FoodMapper.selectById(item.getFoodId());
            item.setFoodDto(FoodEntity!=null?FoodEntity.MapToDto():new FoodDto());              
           
          	            
           //查询出关联的FoodUnit表信息           
            FoodUnit  FoodUnitEntity= FoodUnitMapper.selectById(item.getFoodUnitId());
            item.setFoodUnitDto(FoodUnitEntity!=null?FoodUnitEntity.MapToDto():new FoodUnitDto());              
           
          	            
           //查询出关联的AppUser表信息           
            AppUser  RecordUserEntity= AppUserMapper.selectById(item.getRecordUserId());
            item.setRecordUserDto(RecordUserEntity!=null?RecordUserEntity.MapToDto():new AppUserDto());              
       }
       
     return items; 
   }
  
    /**
     * 饮食记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<DietRecordDto> List(DietRecordPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<DietRecord> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(DietRecord::getCreationTime);
        }

        //构建一个分页查询的model
        Page<DietRecord> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取饮食记录数据
        IPage<DietRecord> pageRecords= DietRecordMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= DietRecordMapper.selectCount(queryWrapper);
        //把DietRecord实体转换成DietRecord传输模型
        List<DietRecordDto> items= Extension.copyBeanList(pageRecords.getRecords(),DietRecordDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个饮食记录查询
     */
    @SneakyThrows
    @Override
    public DietRecordDto Get(DietRecordPagedInput input) {
       if(input.getId()==null)
        {
         return new DietRecordDto();
        }
      
       PagedResult<DietRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new DietRecordDto()); 
    }

    /**
     *饮食记录创建或者修改
     */
    @SneakyThrows
    @Override
    public DietRecordDto CreateOrEdit(DietRecordDto input) {
        //声明一个饮食记录实体
        DietRecord DietRecord=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(DietRecord);
        //把传输模型返回给前端
        return DietRecord.MapToDto();
    }
    /**
     * 饮食记录删除
     */
    @Override
    public void Delete(IdInput input) {
        DietRecord entity = DietRecordMapper.selectById(input.getId());
        DietRecordMapper.deleteById(entity);
    }

    /**
     * 饮食记录批量删除
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
