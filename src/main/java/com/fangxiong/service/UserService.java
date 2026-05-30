package com.fangxiong.service;

import com.fangxiong.vo.Result;

public interface UserService {
    Result getUserPage(Integer pageNum, Integer pageSize, String email, Integer status);
    Result banUser(Integer id, String banReason);
    Result unBanUser(Integer id);
}
