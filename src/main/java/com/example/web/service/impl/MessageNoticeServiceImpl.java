package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.tools.*;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 消息通知功能实现类
 */
@Service
public class MessageNoticeServiceImpl extends ServiceImpl<MessageNoticeMapper, MessageNotice>
        implements MessageNoticeService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的MessageNotice表mapper对象
     */
    @Autowired
    private MessageNoticeMapper MessageNoticeMapper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<MessageNotice> BuilderQuery(MessageNoticePagedInput input) {
        // 声明一个支持消息通知查询的(拉姆达)表达式
        LambdaQueryWrapper<MessageNotice> queryWrapper = Wrappers.<MessageNotice>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, MessageNotice::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTargetKey())) {
            queryWrapper = queryWrapper.like(MessageNotice::getTargetKey, input.getTargetKey());
        }
        if (Extension.isNotNullOrEmpty(input.getResultMsg())) {
            queryWrapper = queryWrapper.like(MessageNotice::getResultMsg, input.getResultMsg());
        }
        if (Extension.isNotNullOrEmpty(input.getType())) {
            queryWrapper = queryWrapper.like(MessageNotice::getType, input.getType());
        }
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
            queryWrapper = queryWrapper.like(MessageNotice::getTitle, input.getTitle());
        }
        if (input.getPlanSendTimeRange() != null && !input.getPlanSendTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(MessageNotice::getPlanSendTime, input.getPlanSendTimeRange().get(1));
            queryWrapper = queryWrapper.ge(MessageNotice::getPlanSendTime, input.getPlanSendTimeRange().get(0));
        }
        if (input.getActualSendTimeRange() != null && !input.getActualSendTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(MessageNotice::getActualSendTime, input.getActualSendTimeRange().get(1));
            queryWrapper = queryWrapper.ge(MessageNotice::getActualSendTime, input.getActualSendTimeRange().get(0));
        }
        if (input.getIsSend() != null) {
            queryWrapper = queryWrapper.eq(MessageNotice::getIsSend, input.getIsSend());
        }
        if (input.getIsSuccess() != null) {
            queryWrapper = queryWrapper.eq(MessageNotice::getIsSuccess, input.getIsSuccess());
        }
        if (Extension.isNotNullOrEmpty(input.getContent())) {
            queryWrapper = queryWrapper.like(MessageNotice::getContent, input.getContent());
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(MessageNotice::getTargetKey, input.getKeyWord()).or()
                    .like(MessageNotice::getResultMsg, input.getKeyWord()).or()
                    .like(MessageNotice::getType, input.getKeyWord()).or()
                    .like(MessageNotice::getTitle, input.getKeyWord()).or()
                    .like(MessageNotice::getContent, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理消息通知对于的外键数据
     */
    private List<MessageNoticeDto> DispatchItem(List<MessageNoticeDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (MessageNoticeDto item : items) {
        }

        return items;
    }

    /**
     * 消息通知分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<MessageNoticeDto> List(MessageNoticePagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<MessageNotice> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(MessageNotice::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<MessageNotice> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取消息通知数据
        IPage<MessageNotice> pageRecords = MessageNoticeMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = MessageNoticeMapper.selectCount(queryWrapper);
        // 把MessageNotice实体转换成MessageNotice传输模型
        List<MessageNoticeDto> items = Extension.copyBeanList(pageRecords.getRecords(), MessageNoticeDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个消息通知查询
     */
    @SneakyThrows
    @Override
    public MessageNoticeDto Get(MessageNoticePagedInput input) {
        if (input.getId() == null) {
            return new MessageNoticeDto();
        }

        PagedResult<MessageNoticeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new MessageNoticeDto());
    }

    /**
     * 消息通知创建或者修改
     */
    @SneakyThrows
    @Override
    public MessageNoticeDto CreateOrEdit(MessageNoticeDto input) {
        // 声明一个消息通知实体
        MessageNotice MessageNotice = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(MessageNotice);
        // 把传输模型返回给前端
        return MessageNotice.MapToDto();
    }

    /**
     * 消息通知删除
     */
    @Override
    public void Delete(IdInput input) {
        MessageNotice entity = MessageNoticeMapper.selectById(input.getId());
        MessageNoticeMapper.deleteById(entity);
    }

    /**
     * 消息通知批量删除
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
     * 自动发送邮件
     */
    @Override
    public void AutoSendEmail() {
        LambdaQueryWrapper<MessageNotice> queryWrapper = Wrappers.<MessageNotice>lambdaQuery()
                .eq(MessageNotice::getIsSend, false)
                .eq(MessageNotice::getType, "邮箱")
                .le(MessageNotice::getPlanSendTime, LocalDateTime.now());
        List<MessageNotice> messageNotices = MessageNoticeMapper.selectList(queryWrapper);
        for (MessageNotice messageNotice : messageNotices) {

            var result = EmailUtil.sendTextMail(messageNotice.getTargetKey(), messageNotice.getTitle(),
                    messageNotice.getContent());

            messageNotice.setIsSend(true);
            messageNotice.setActualSendTime(LocalDateTime.now());
            messageNotice.setIsSuccess(result);
            messageNotice.setResultMsg(result ? "发送成功" : "发送失败");
            MessageNoticeMapper.updateById(messageNotice);
        }
    }

    /**
     * 添加发送邮箱的记录
     */
    @Override
    public void AddSendEmailRecord(Integer UserId, String Title, String Content, LocalDateTime PlanSendTime) {

        // 查询用户的邮箱
        AppUser appUser = AppUserMapper.selectById(UserId);
        if (appUser != null && Extension.isNotNullOrEmpty(appUser.getEmail())) {

            MessageNotice messageNotice = new MessageNotice();
            messageNotice.setUserId(UserId);
            messageNotice.setTitle(Title);
            messageNotice.setContent(Content);
            messageNotice.setType("邮箱");
            messageNotice.setTargetKey(appUser.getEmail());
            messageNotice.setIsSend(false);
            messageNotice.setPlanSendTime(PlanSendTime);
            MessageNoticeMapper.insert(messageNotice);
        }
    }
}
