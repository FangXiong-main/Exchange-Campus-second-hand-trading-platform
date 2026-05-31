package com.fangxiong.service.impl;

import com.fangxiong.Utils.BCryptPasswordUtil;
import com.fangxiong.Utils.JWTUtils;
import com.fangxiong.Utils.RandomCodeGenerator;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        if (!BCryptPasswordUtil.matches(password, dbPassword)) {
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
            userMapper.insert(user);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());
        String token = JWTUtils.generateToken(claims);
        redisUtils.setStringValue(TOKEN_KEY + user.getId(), new LoginToken(user.getUsername(), token), TOKEN_EXPIRE_TIME);
        log.info("验证码登录成功，正在保存Token至Redis");
        LoginResult loginResult = new LoginResult();
        loginResult.setId(user.getId());
        loginResult.setUsername(user.getUsername());
        loginResult.setRole(user.getRole());
        loginResult.setToken(token);
        loginResult.setSchool(user.getSchool());
        return Result.success(loginResult);
    }

    @Override
    public Result loginWithEmail(LoginDTO loginDTO) {
        return null;
    }
}