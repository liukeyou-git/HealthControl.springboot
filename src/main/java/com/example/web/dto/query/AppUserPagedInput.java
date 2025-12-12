package com.example.web.dto.query;

import com.example.web.tools.dto.PagedInput;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AppUserPagedInput extends PagedInput {
    private Integer Id;

    /**
     * 姓名
     */
    @JsonProperty("Name")
    private String Name;

    /**
     * 邮箱
     */
    @JsonProperty("Email")
    private String Email;

    /**
     * 手机号码
     */
    @JsonProperty("PhoneNumber")
    private String PhoneNumber;

    /**
     * 账号
     */
    @JsonProperty("UserName")
    private String UserName;

    /**
     * 账号角色
     */
    @JsonProperty("RoleType")
    private Integer RoleType;
}
