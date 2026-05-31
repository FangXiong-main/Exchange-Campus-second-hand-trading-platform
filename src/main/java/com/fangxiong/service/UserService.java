package com.fangxiong.service;

import com.fangxiong.dto.LoginResult;
import com.fangxiong.vo.Result;

public interface UserService {
    Result getUserPage(Integer pageNum, Integer pageSize, String email, Integer status);
    Result banUser(Long id, String banReason);
    Result unBanUser(Long id);

    Result getCurrentUser(LoginResult currentUserInfo);
}
