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
 * 健康提醒功能的Service接口的定义清单
 */
public interface HealthNoticeService extends IService<HealthNotice> {

    /**
     * 健康提醒的分页查询方法接口定义
     */
    public PagedResult<HealthNoticeDto> List(HealthNoticePagedInput input) ;
    /**
     * 健康提醒的新增或者修改方法接口定义
     */
    public HealthNoticeDto CreateOrEdit(HealthNoticeDto input);

     /**
     * 获取健康提醒信息
     */
    public HealthNoticeDto Get(HealthNoticePagedInput input);
 	 /**
     * 健康提醒删除
     */
    public void Delete(IdInput input);

    /**
     * 健康提醒批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
