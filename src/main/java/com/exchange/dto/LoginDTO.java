package com.exchange.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String code;
    private Integer role; // 1=普通用户 2=管理员
}