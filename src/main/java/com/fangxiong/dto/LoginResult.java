package com.fangxiong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
    private Integer id;
    private String username;
    private String token;
    private Integer role;
}
