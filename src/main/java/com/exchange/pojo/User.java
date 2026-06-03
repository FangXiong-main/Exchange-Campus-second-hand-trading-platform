package com.exchange.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Long school;
    private String banReason;
    private Integer role;
    private BigDecimal balance;
    private String avatarUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}