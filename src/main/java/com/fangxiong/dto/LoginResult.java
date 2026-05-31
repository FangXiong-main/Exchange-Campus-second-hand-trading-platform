package com.fangxiong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
    private Long id;
    private String username;
    private String token;
    private Integer role;
    private String school;
    private String avatarUrl;
}
