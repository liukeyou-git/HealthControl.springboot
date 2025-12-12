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
import java.time.LocalDateTime;

/**
 * 消息通知功能的Service接口的定义清单
 */
public interface MessageNoticeService extends IService<MessageNotice> {

    /**
     * 消息通知的分页查询方法接口定义
     */
    public PagedResult<MessageNoticeDto> List(MessageNoticePagedInput input);

    /**
     * 消息通知的新增或者修改方法接口定义
     */
    public MessageNoticeDto CreateOrEdit(MessageNoticeDto input);

    /**
     * 获取消息通知信息
     */
    public MessageNoticeDto Get(MessageNoticePagedInput input);

    /**
     * 消息通知删除
     */
    public void Delete(IdInput input);

    /**
     * 消息通知批量删除
     */
    public void BatchDelete(IdsInput input);

    /**
     * 自动发送邮件
     */
    public void AutoSendEmail();

    /**
     * 添加发送邮箱的记录
     */
    public void AddSendEmailRecord(Integer UserId, String Title, String Content, LocalDateTime PlanSendTime);
}
