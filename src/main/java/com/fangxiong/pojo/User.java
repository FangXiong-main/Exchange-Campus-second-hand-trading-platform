package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String school;
    private String banReason;
    private Integer role;
    private BigDecimal balance;
    private String avatarUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}