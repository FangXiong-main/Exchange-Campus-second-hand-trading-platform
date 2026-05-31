package com.fangxiong.service.impl;

import com.fangxiong.dto.LoginResult;
import com.fangxiong.mapper.UserMapper;
import com.fangxiong.pojo.User;
import com.fangxiong.service.UserService;
import com.fangxiong.vo.PageResult;
import com.fangxiong.vo.Result;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public Result getUserPage(Integer pageNum, Integer pageSize, String email, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.getUserList(email, status);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setRows(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return Result.success(pageResult);
    }

    @Override
    public Result banUser(Long id, String banReason) {
        User user = new User();
        user.setId(id);
        user.setRole(-1);
        user.setBanReason(banReason);
        userMapper.banUser(user.getId(), user.getBanReason());
        return Result.success("封禁成功");
    }

    @Override
    public Result unBanUser(Long id) {
        userMapper.unBanUser(id);
        return Result.success();
    }

    @Override
    public Result getCurrentUser(LoginResult currentUserInfo) {
        User user = userMapper.selectById(currentUserInfo.getId());
        return Result.success(user);
    }
}
