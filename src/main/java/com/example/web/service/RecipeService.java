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
 * 食谱功能的Service接口的定义清单
 */
public interface RecipeService extends IService<Recipe> {

    /**
     * 食谱的分页查询方法接口定义
     */
    public PagedResult<RecipeDto> List(RecipePagedInput input);

    /**
     * 食谱的新增或者修改方法接口定义
     */
    public RecipeDto CreateOrEdit(RecipeDto input);

    /**
     * 获取食谱信息
     */
    public RecipeDto Get(RecipePagedInput input);

    /**
     * 食谱删除
     */
    public void Delete(IdInput input);

    /**
     * 食谱批量删除
     */
    public void BatchDelete(IdsInput input);

    /**
     * 食谱审核
     */
    public void Audit(RecipeDto input);

    /**
     * 食谱浏览次数增加
     */
    public void AddViewCount(RecipeDto input);

    /**
     * 推荐算法(基于行为+权重的协同过滤算法)
     */
    public List<RecipeDto> RecommendList(RecipePagedInput input);
}
