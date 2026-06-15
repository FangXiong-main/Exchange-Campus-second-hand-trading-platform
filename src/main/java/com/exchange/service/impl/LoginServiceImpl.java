package com.exchange.service.impl;

import com.exchange.Utils.BCryptPasswordUtil;
import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.JWTUtils;
import com.exchange.Utils.RandomCodeGenerator;
import com.exchange.dto.LoginToken;
import com.exchange.mapper.UserMapper;
import com.exchange.dto.LoginResult;
import com.exchange.vo.Result;
import com.exchange.dto.LoginDTO;
import com.exchange.pojo.User;
import com.exchange.service.LoginService;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.exchange.constants.SystemConstants.*;


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
        if (user.getRole() != 2) {
            return Result.error("非管理员用户");
        }

        String dbPassword = user.getPassword();
        if (!BCryptPasswordUtil.matches(password, dbPassword)) {
            return Result.error("密码错误");
        }

        LoginResult loginResult = new LoginResult();
        loginResult.setId(user.getId());
        loginResult.setUsername(username);
        loginResult.setRole(user.getRole());
        loginResult.setSchool(user.getSchool());
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
        String code = loginDTO.getCode();
        String redisCode = redisUtils.getStringValue(EMAIL_KEY + loginDTO.getEmail(), String.class);
        if (redisCode == null || !redisCode.equals(code)) {
            return Result.error("验证码错误");
        }
        redisUtils.remove(EMAIL_KEY+loginDTO.getEmail());
        User user = userMapper.selectByEmail(loginDTO);
        if (user == null) {
            log.info("新用户，自动注册");
            user = new User();
            user.setEmail(loginDTO.getEmail());
            user.setUsername(DEFAULT_NAME_PREFIX+ RandomCodeGenerator.generateCode(6));
            user.setBalance(BigDecimal.ZERO);
            user.setPassword(BCryptPasswordUtil.encode(RandomCodeGenerator.generateCode(12)));
            user.setRole(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setAvatarUrl(EXCHANGE_DEFAULT_AVATAR_URL);
            userMapper.insert(user);
        }
        return checkUserAndSaveToken(user);
    }

    @Override
    public Result loginWithEmail(LoginDTO loginDTO) {
        User user = userMapper.selectByEmail(loginDTO);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!BCryptPasswordUtil.matches(loginDTO.getPassword(), user.getPassword())){
            return Result.error("密码错误");
        }
        return checkUserAndSaveToken(user);
    }

    @Override
    public Boolean emailAndUserIsEqual(String email) {
        User byEmail = userMapper.findByEmail(email);
        return byEmail != null && byEmail.getId().equals(CurrentHolder.getCurrentUserInfo().getId());
    }

    private Result checkUserAndSaveToken(User user){
        if (user.getRole()==-1){
            return Result.error("您的账号已被被封禁，原因："+user.getBanReason());
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());
        String token = JWTUtils.generateToken(claims);
        redisUtils.setStringValue(TOKEN_KEY + user.getId(), new LoginToken(user.getUsername(), token), USER_TOKEN_EXPIRE_TIME);
        log.info("密码登录成功，正在保存Token至Redis");
        LoginResult loginResult = new LoginResult();
        loginResult.setId(user.getId());
        loginResult.setUsername(user.getUsername());
        loginResult.setRole(user.getRole());
        loginResult.setToken(token);
        loginResult.setSchool(user.getSchool());
        loginResult.setAvatarUrl(user.getAvatarUrl());
        return Result.success(loginResult);
    }

}