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
 * 健康知识分类功能的Service接口的定义清单
 */
public interface HealthArticleTypeService extends IService<HealthArticleType> {

    /**
     * 健康知识分类的分页查询方法接口定义
     */
    public PagedResult<HealthArticleTypeDto> List(HealthArticleTypePagedInput input) ;
    /**
     * 健康知识分类的新增或者修改方法接口定义
     */
    public HealthArticleTypeDto CreateOrEdit(HealthArticleTypeDto input);

     /**
     * 获取健康知识分类信息
     */
    public HealthArticleTypeDto Get(HealthArticleTypePagedInput input);
 	 /**
     * 健康知识分类删除
     */
    public void Delete(IdInput input);

    /**
     * 健康知识分类批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
