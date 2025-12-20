package com.example.web.tools.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API请求DTO
 */
@Data
public class DeepSeekRequestDto {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Message> messages;

    /**
     * 响应格式
     */
    private Map<String, String> response_format;

    /**
     * 最大令牌数
     */
    private Integer max_tokens;

    /**
     * 温度参数
     */
    private Double temperature;

    @Data
    public static class Message {
        /**
         * 角色 (system/user/assistant)
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }
}