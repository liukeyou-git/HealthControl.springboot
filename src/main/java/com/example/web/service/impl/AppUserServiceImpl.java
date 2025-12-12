package com.example.web.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.AppUserDto;
import com.example.web.dto.query.AppUserPagedInput;
import com.example.web.entity.AppUser;
import com.example.web.mapper.AppUserMapper;
import com.example.web.service.AppUserService;
import com.example.web.tools.Extension;
import com.example.web.tools.JWTUtils;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;
import com.example.web.tools.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

/**
 * 账号功能实现类
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    /**
     * 操作数据库的账号表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMpper;

    @Override
    @SneakyThrows
    public PagedResult<AppUserDto> List(AppUserPagedInput input) {
        // 声明一个支持账号查询的(拉姆达)表达式
        LambdaQueryWrapper<AppUser> queryWrapper = Wrappers.<AppUser>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, AppUser::getId, input.getId());

        // 如果前端搜索传入Name不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getName())) {
            queryWrapper = queryWrapper.like(AppUser::getName, input.getName());
        }

        // 如果前端搜索传入Email不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getEmail())) {
            queryWrapper = queryWrapper.like(AppUser::getEmail, input.getEmail());
        }

        // 如果前端搜索传入getPhoneNumber不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getPhoneNumber())) {
            queryWrapper = queryWrapper.like(AppUser::getPhoneNumber, input.getPhoneNumber());
        }

        if (Extension.isNotNullOrEmpty(input.getUserName())) {
            queryWrapper = queryWrapper.eq(AppUser::getUserName, input.getUserName());
        }
        // 如果前端搜索传入getRoleType不为空,则进行精确查询
        if (input.getRoleType() != null) {
            queryWrapper = queryWrapper.eq(AppUser::getRoleType, input.getRoleType());
        }

        // 按创建时间从大到小排序 最新的显示在最前面
        queryWrapper = queryWrapper.orderByDesc(AppUser::getCreationTime);
        // 构建一个分页查询的model
        Page<AppUser> page = new Page<>(input.getPage(), input.getLimit());

        // 从数据库进行分页查询获取账号数据
        IPage<AppUser> pageRecords = AppUserMpper.selectPage(page, queryWrapper);

        // 获取所有满足条件的数据行数
        Long totalCount = AppUserMpper.selectCount(queryWrapper);

        // 把账号实体转换成账号传输模型
        List<AppUserDto> items = Extension.copyBeanList(pageRecords.getRecords(), AppUserDto.class);
        for (AppUserDto item : items) {
            // 不需要把密码暴露出去
            item.setPassword(null);
        }
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);
    }

    /**
     * 账号创建或者修改
     */
    @SneakyThrows
    @Override
    public AppUserDto CreateOrEdit(AppUserDto input) {

        if (Extension.isNullOrEmpty(input.getUserName())) {
            throw new CustomException("账号名不能为空");
        }
        if (input.IsAdd() && Extension.isNullOrEmpty(input.getPassword())) {
            throw new CustomException("密码不能为空");
        }

        // 如果前端传入新的密码且不是MD5加密的，则进行MD5加密
        if (Extension.isNotNullOrEmpty(input.getPassword()) && !Extension.IsMD5Encrypt(input.getPassword())) {
            input.setPassword(Extension.MD5Encrypt(input.getPassword()));
        }
        // 如果前端传入的密码为空且
        if (Extension.isNullOrEmpty(input.getPassword())) {
            // 查询账号
            AppUser appUser = AppUserMpper.selectById(input.getId());
            input.setPassword(appUser.getPassword());
        }

        AppUser appUser = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(appUser);
        // 把传输模型返回给前端
        return appUser.MapToDto();
    }

    /**
     * 账号删除
     */
    @Override
    public void Delete(IdInput input) {
        AppUser entity = AppUserMpper.selectById(input.getId());
        AppUserMpper.deleteById(entity);
    }

    /**
     * 账号批量删除
     */
    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            Delete(idInput);
        }
    }

    /**
     * 账号单个查询
     */
    @Override
    public AppUserDto Get(AppUserPagedInput input) {
        if (input.getId() == null) {
            return new AppUserDto();
        }
        return List(input).getItems().stream().findFirst().orElse(new AppUserDto());
    }

   /**
     * 登录
     */
    public String SignIn(AppUserDto input) {
        LambdaQueryWrapper<AppUser> queryWrapper = Wrappers.<AppUser>lambdaQuery();
        if (Extension.isNotNullOrEmpty(input.getUserName())) {
            queryWrapper.eq(AppUser::getUserName, input.getUserName());
        }
        if (Extension.isNotNullOrEmpty(input.getPassword())) {
            queryWrapper.eq(AppUser::getPassword, Extension.MD5Encrypt(input.getPassword()));
        }
        if (input.getRoleType() != null) {
            queryWrapper.eq(AppUser::getRoleType, input.getRoleType());
        }

        List<AppUser> items = AppUserMpper.selectList(queryWrapper);
        if (items.stream().count() == 0) {
            throw new CustomException("请检查登录的账号或者密码,角色是否都正确!");
        }
        Map<String, String> map = new HashMap<>();
        map.put(SysConst.UserIdClaim, items.get(0).getId().toString());
        map.put(SysConst.RoleTypeClaim, items.get(0).getRoleType().toString());
        String token = JWTUtils.getToken(map);
        return token;
    }

    /**
     * 注册
     */
    @Override
    public AppUserDto Register(AppUserDto input) {

        // 对密码进行MD5加密
        input.setPassword(Extension.MD5Encrypt(input.getPassword()));
        // 检查账号名是否存在
        Long userCount = AppUserMpper.selectCount(Wrappers.<AppUser>lambdaQuery()
                .eq(Extension.isNotNullOrEmpty(input.getUserName()), AppUser::getUserName, input.getUserName()));
        if (userCount > 0) {
            throw new CustomException("该账号名已经存在!");
        }
        if (Extension.isNotNullOrEmpty(input.getEmail())) {
            // 检查邮箱是否存在
            Long emailCount = AppUserMpper.selectCount(Wrappers.<AppUser>lambdaQuery()
                    .eq(Extension.isNotNullOrEmpty(input.getEmail()), AppUser::getEmail, input.getEmail()));
            if (emailCount > 0) {
                throw new CustomException("该邮箱已经存在!");
            }
        }
        if (Extension.isNotNullOrEmpty(input.getPhoneNumber())) {
            // 检查手机号是否存在
            Long phoneCount = AppUserMpper.selectCount(Wrappers.<AppUser>lambdaQuery()
                    .eq(Extension.isNotNullOrEmpty(input.getPhoneNumber()), AppUser::getPhoneNumber,
                            input.getPhoneNumber()));
            if (phoneCount > 0) {
                throw new CustomException("该手机号已经存在!");
            }
        }
        return CreateOrEdit(input);
    }

    /**
     * 找回密码
     */
    @Override
    public void ForgetPassword(AppUserDto input) {

        // 检查账号名是否存在
        AppUser appUser = AppUserMpper.selectList(Wrappers.<AppUser>lambdaQuery()
                .eq(Extension.isNotNullOrEmpty(input.getUserName()), AppUser::getUserName, input.getUserName()))
                .stream().findFirst().orElse(null);

        if (appUser == null) {
            throw new CustomException("该账号名不存在!");
        }
        if (appUser.getPhoneNumber() == null) {
            throw new CustomException("该账号未绑定手机号!");
        }
        if (Extension.isNotNullOrEmpty(input.getEmail())&& appUser.getEmail() == null) {
            throw new CustomException("该账号未绑定邮箱!");
        }
        if (!appUser.getPhoneNumber().equals(input.getPhoneNumber())) {
            throw new CustomException("请输入你绑定的手机号!");
        }
        if (input.getEmail() != null && !input.getEmail().equals(appUser.getEmail())) {
            throw new CustomException("请输入你绑定的邮箱!");
        }
        appUser.setPassword(Extension.MD5Encrypt(input.getPassword()));
        saveOrUpdate(appUser);
    }

    /** 修改密码 */
    @Override
    public void ChangePassword(AppUserDto input) {
        AppUser appUser = AppUserMpper.selectById(input.getId());
        if (!Extension.MD5Encrypt(input.getOrginPassword()).equals(appUser.getPassword())) {
            throw new CustomException("旧密码错误!");
        }
        // 对当前输入的密码进行加密
        if (Extension.MD5Encrypt(input.getPassword()).equals(appUser.getPassword())) {
            throw new CustomException("新密码不能与旧密码相同!");
        }

        appUser.setPassword(Extension.MD5Encrypt(input.getPassword()));
        saveOrUpdate(appUser);
    }

    /**
     * 重置密码
     */
    @Override
    public void ResetPassword(AppUserDto input) {
        AppUser appUser = AppUserMpper.selectById(input.getId());
        appUser.setPassword(Extension.MD5Encrypt(appUser.getUserName()));
        saveOrUpdate(appUser);
    }

    /**
     * 账号导出
     */
    @Override
    public void Export(@RequestParam String query, HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        AppUserPagedInput input = mapper.readValue(query, AppUserPagedInput.class);

        List<AppUserDto> items = List(input).getItems();

        // 声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 生成一个表格，设置表格名称为"账号表"
        HSSFSheet sheet = workbook.createSheet("账号表");

        // 设置表格列宽度为10个字节
        sheet.setDefaultColumnWidth(10);
        // 创建标题的显示样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 创建第一行表头
        HSSFRow headrow = sheet.createRow(0);

        // 表头数据
        String[] header = { "账户", "姓名", "邮箱", "手机号码", "账号角色", "出生年月" };
        // 遍历添加表头(下面模拟遍历账号，也是同样的操作过程)
        for (int i = 0; i < header.length; i++) {
            // 创建一个单元格
            HSSFCell cell = headrow.createCell(i);

            // 创建一个内容对象
            HSSFRichTextString text = new HSSFRichTextString(header[i]);

            // 将内容对象的文字内容写入到单元格中
            cell.setCellValue(text);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < items.size(); i++) {

            AppUserDto appUser = items.get(i);

            // 创建一行
            HSSFRow row = sheet.createRow(i + 1);

            if (appUser.getUserName() != null) {
                row.createCell(0).setCellValue(new HSSFRichTextString(appUser.getUserName()));
            }
            if (appUser.getName() != null) {
                row.createCell(1).setCellValue(new HSSFRichTextString(appUser.getName()));
            }
            if (appUser.getEmail() != null) {
                row.createCell(2).setCellValue(new HSSFRichTextString(appUser.getEmail()));
            }
            if (appUser.getPhoneNumber() != null) {
                row.createCell(3).setCellValue(new HSSFRichTextString(appUser.getPhoneNumber()));
            }
            if (appUser.getRoleType() != null) {
                row.createCell(4).setCellValue(new HSSFRichTextString(appUser.RoleTypeFormat()));
            }
            if (appUser.getBirth() != null) {
                row.createCell(5).setCellValue(
                        new HSSFRichTextString(Extension.LocalDateTimeConvertString(appUser.getBirth(), null)));
            }
        }

        // 准备将Excel的输出流通过response输出到页面下载
        // 八进制输出流
        response.setContentType("application/octet-stream");

        // 这后面可以设置导出Excel的名称
        response.setHeader("Content-disposition", "attachment;filename=" + System.currentTimeMillis() + ".xls");

        // 刷新缓冲
        response.flushBuffer();

        // workbook将Excel写入到response的输出流中，供页面下载
        workbook.write(response.getOutputStream());
    }
}
