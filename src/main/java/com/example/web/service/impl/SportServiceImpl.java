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
 * 运动参考功能实现类
 */
@Service
public class SportServiceImpl extends ServiceImpl<SportMapper, Sport> implements SportService {

	 /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的Sport表mapper对象
     */
    @Autowired
    private SportMapper SportMapper;

  
   /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Sport> BuilderQuery(SportPagedInput input) {
       //声明一个支持运动参考查询的(拉姆达)表达式
        LambdaQueryWrapper<Sport> queryWrapper = Wrappers.<Sport>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Sport::getId, input.getId());
   //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getContent())) {
             queryWrapper = queryWrapper.like(Sport::getContent, input.getContent());
       	 }
        if (Extension.isNotNullOrEmpty(input.getName())) {
             queryWrapper = queryWrapper.like(Sport::getName, input.getName());
       	 }
      

 
 
     if(Extension.isNotNullOrEmpty(input.getKeyWord()))
        {
			queryWrapper=queryWrapper.and(i->i
          	   .like(Sport::getContent,input.getKeyWord()).or()   	 
          	   .like(Sport::getName,input.getKeyWord()).or()   	 
        );
                                       
 		   }
    
      return queryWrapper;
    }
  
    /**
     * 处理运动参考对于的外键数据
     */
   private List<SportDto> DispatchItem(List<SportDto> items) throws InvocationTargetException, IllegalAccessException {
          
       for (SportDto item : items) {       }
       
     return items; 
   }
  
    /**
     * 运动参考分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<SportDto> List(SportPagedInput input) {
			//构建where条件+排序
        LambdaQueryWrapper<Sport> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Sport::getCreationTime);
        }

        //构建一个分页查询的model
        Page<Sport> page = new Page<>(input.getPage(), input.getLimit());
         //从数据库进行分页查询获取运动参考数据
        IPage<Sport> pageRecords= SportMapper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount= SportMapper.selectCount(queryWrapper);
        //把Sport实体转换成Sport传输模型
        List<SportDto> items= Extension.copyBeanList(pageRecords.getRecords(),SportDto.class);

		   DispatchItem(items);
        //返回一个分页结构给前端
        return PagedResult.GetInstance(items,totalCount);

    }
  
    /**
     * 单个运动参考查询
     */
    @SneakyThrows
    @Override
    public SportDto Get(SportPagedInput input) {
       if(input.getId()==null)
        {
         return new SportDto();
        }
      
       PagedResult<SportDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new SportDto()); 
    }

    /**
     *运动参考创建或者修改
     */
    @SneakyThrows
    @Override
    public SportDto CreateOrEdit(SportDto input) {
        //声明一个运动参考实体
        Sport Sport=input.MapToEntity();  
        //调用数据库的增加或者修改方法
        saveOrUpdate(Sport);
        //把传输模型返回给前端
        return Sport.MapToDto();
    }
    /**
     * 运动参考删除
     */
    @Override
    public void Delete(IdInput input) {
        Sport entity = SportMapper.selectById(input.getId());
        SportMapper.deleteById(entity);
    }

    /**
     * 运动参考批量删除
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
