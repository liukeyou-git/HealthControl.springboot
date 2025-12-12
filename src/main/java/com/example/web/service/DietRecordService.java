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
 * 饮食记录功能的Service接口的定义清单
 */
public interface DietRecordService extends IService<DietRecord> {

    /**
     * 饮食记录的分页查询方法接口定义
     */
    public PagedResult<DietRecordDto> List(DietRecordPagedInput input) ;
    /**
     * 饮食记录的新增或者修改方法接口定义
     */
    public DietRecordDto CreateOrEdit(DietRecordDto input);

     /**
     * 获取饮食记录信息
     */
    public DietRecordDto Get(DietRecordPagedInput input);
 	 /**
     * 饮食记录删除
     */
    public void Delete(IdInput input);

    /**
     * 饮食记录批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
