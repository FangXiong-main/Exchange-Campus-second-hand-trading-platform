package com.exchange.service;


import com.exchange.dto.LoginResult;
import com.exchange.vo.Result;
import com.exchange.dto.LoginDTO;

public interface LoginService {
    Result adminLogin(LoginDTO loginDTO);

    Result logout(LoginResult loginResult);

    Result loginWithCode(LoginDTO loginDTO);

    Result loginWithEmail(LoginDTO loginDTO);
}