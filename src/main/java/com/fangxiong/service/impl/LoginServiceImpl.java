package com.fangxiong.service.impl;

import com.fangxiong.Utils.JWTUtils;
import com.fangxiong.dto.LoginToken;
import com.fangxiong.mapper.UserMapper;
import com.fangxiong.dto.LoginResult;
import com.fangxiong.vo.Result;
import com.fangxiong.dto.LoginDTO;
import com.fangxiong.pojo.User;
import com.fangxiong.service.LoginService;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.fangxiong.constants.SystemConstants.*;


@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserMapper userMapper;

    @Override
    public Result adminLogin(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error("账号、密码不能为空");
        }

        User user = userMapper.selectByName(loginDTO);
        if (user == null) {
            return Result.error("管理员不存在");
        }

        String dbPassword = user.getPassword();
        if (!BCrypt.checkpw(password, dbPassword)) {
            return Result.error("密码错误");
        }

        LoginResult loginResult = new LoginResult();
        loginResult.setId(user.getId());
        loginResult.setUsername(username);
        loginResult.setRole(user.getRole());
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("id", user.getId());
        String token = JWTUtils.generateToken(claims);
        loginResult.setToken(token);
        log.info("登录成功，正在保存Token至Redis");
        redisUtils.setStringValue(TOKEN_KEY + user.getId(), new LoginToken(user.getUsername(), token), TOKEN_EXPIRE_TIME);

        return Result.success(loginResult);
    }

    @Override
    public Result logout(LoginResult loginResult) {
        redisUtils.remove(TOKEN_KEY+loginResult.getId());
        return Result.success();
    }

    @Override
    public Result loginWithCode(LoginDTO loginDTO) {

        return null;
    }

    @Override
    public Result loginWithEmail(LoginDTO loginDTO) {
        return null;
    }
}