package com.fangxiong.service;


import com.fangxiong.dto.LoginResult;
import com.fangxiong.vo.Result;
import com.fangxiong.dto.LoginDTO;

public interface LoginService {
    Result adminLogin(LoginDTO loginDTO);

    Result logout(LoginResult loginResult);

    Result loginWithCode(LoginDTO loginDTO);

    Result loginWithEmail(LoginDTO loginDTO);
}