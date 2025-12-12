package com.example.web.service;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.AppUserDto;
import com.example.web.dto.query.AppUserPagedInput;
import com.example.web.entity.AppUser;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 账号功能的Service接口的定义清单
 */
public interface AppUserService extends IService<AppUser> {

    /**
     * 账号的分页查询方法接口定义
     */
    public PagedResult<AppUserDto> List(AppUserPagedInput input);

    /**
     * 账号的新增或者修改方法接口定义
     */
    public AppUserDto CreateOrEdit(AppUserDto input);

    /**
     * 账号模块删除
     */
    public void Delete(IdInput input);

    /**
     * 账号模块批量删除
     */
    public void BatchDelete(IdsInput input);

    /**
     * 查询单个对象
     */
    public AppUserDto Get(AppUserPagedInput input);

    /**
     * 账号登录
     */
    public String SignIn(AppUserDto input);

    /**
     * 注册
     */
    public AppUserDto Register(AppUserDto input);

    /**
     * 找回密码
     */
    void ForgetPassword(AppUserDto input);

    /**
     * 修改密码
     */
    void ChangePassword(AppUserDto input);

    /**
     * 重置密码
     */
    void ResetPassword(AppUserDto input);

    /**
     * 账号导出
     */
    public void Export(@RequestParam String query, HttpServletResponse response) throws IOException;

}
