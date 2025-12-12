package com.example.web.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.web.service.MessageNoticeService;

@Component

public class MessageNoticeJob {

    @Autowired
    private MessageNoticeService MessageNoticeService;

    // 每隔10s执行一次
    @Scheduled(cron = "0/10 * * * * ?")
    public void autoSendEmail() {
        MessageNoticeService.AutoSendEmail();
    }
}
