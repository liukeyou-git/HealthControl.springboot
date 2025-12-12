package com.example.web.tools;

import jakarta.annotation.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {


    private static JavaMailSender javaMailSender;

    private static final String CommEmail="806105498@qq.com";
    @Resource
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        EmailUtil.javaMailSender = javaMailSender;
    }

    /**
     * 发送普通文本
     *
     * @param from        发送人
     * @param to          接收人
     * @param subject     主题
     * @param mailContent 正文内容
     * @author kevin

     */
    public static boolean sendTextMail(String from, String to, String subject, String mailContent) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(mailContent);
            javaMailSender.send(message);
            return Boolean.TRUE;
        } catch (MailException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
    /**
     * 发送普通文本
     *
     * @param to          接收人
     * @param subject     主题
     * @param mailContent 正文内容
     * @author kevin
     */
    public static boolean sendTextMail( String to, String subject, String mailContent) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EmailUtil.CommEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(mailContent);
            javaMailSender.send(message);
            return Boolean.TRUE;
        } catch (MailException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

}
