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
 * 运动参考功能的Service接口的定义清单
 */
public interface SportService extends IService<Sport> {

    /**
     * 运动参考的分页查询方法接口定义
     */
    public PagedResult<SportDto> List(SportPagedInput input) ;
    /**
     * 运动参考的新增或者修改方法接口定义
     */
    public SportDto CreateOrEdit(SportDto input);

     /**
     * 获取运动参考信息
     */
    public SportDto Get(SportPagedInput input);
 	 /**
     * 运动参考删除
     */
    public void Delete(IdInput input);

    /**
     * 运动参考批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
