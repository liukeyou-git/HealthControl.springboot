package com.example.web.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.tools.dto.*;
import com.example.web.enums.*;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 食物单位功能的Service接口的定义清单
 */
public interface FoodUnitService extends IService<FoodUnit> {

    /**
     * 食物单位的分页查询方法接口定义
     */
    public PagedResult<FoodUnitDto> List(FoodUnitPagedInput input) ;
    /**
     * 食物单位的新增或者修改方法接口定义
     */
    public FoodUnitDto CreateOrEdit(FoodUnitDto input);

     /**
     * 获取食物单位信息
     */
    public FoodUnitDto Get(FoodUnitPagedInput input);
 	 /**
     * 食物单位删除
     */
    public void Delete(IdInput input);

    /**
     * 食物单位批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
