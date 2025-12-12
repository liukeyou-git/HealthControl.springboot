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
 * 点赞记录功能的Service接口的定义清单
 */
public interface LikeRecordService extends IService<LikeRecord> {

    /**
     * 点赞记录的分页查询方法接口定义
     */
    public PagedResult<LikeRecordDto> List(LikeRecordPagedInput input) ;
    /**
     * 点赞记录的新增或者修改方法接口定义
     */
    public LikeRecordDto CreateOrEdit(LikeRecordDto input);

     /**
     * 获取点赞记录信息
     */
    public LikeRecordDto Get(LikeRecordPagedInput input);
 	 /**
     * 点赞记录删除
     */
    public void Delete(IdInput input);

    /**
     * 点赞记录批量删除
     */
    public void BatchDelete(IdsInput input);
  

}
