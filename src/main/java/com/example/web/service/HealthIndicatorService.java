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
 * 健康指标功能的Service接口的定义清单
 */
public interface HealthIndicatorService extends IService<HealthIndicator> {

    /**
     * 健康指标的分页查询方法接口定义
     */
    public PagedResult<HealthIndicatorDto> List(HealthIndicatorPagedInput input) ;
    /**
     * 健康指标的新增或者修改方法接口定义
     */
    public HealthIndicatorDto CreateOrEdit(HealthIndicatorDto input);

     /**
     * 获取健康指标信息
     */
    public HealthIndicatorDto Get(HealthIndicatorPagedInput input);
 	 /**
     * 健康指标删除
     */
    public void Delete(IdInput input);

    /**
     * 健康指标批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
