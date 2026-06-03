package com.exchange.controller;

import com.exchange.Utils.CodeUtil;
import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.EmailUtil;
import com.exchange.dto.LoginResult;
import com.exchange.vo.Result;
import com.exchange.dto.LoginDTO;
import com.exchange.service.LoginService;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.exchange.constants.SystemConstants.*;


@Slf4j
@RestController
public class LoginController {

    // 注入Service
    @Resource
    private LoginService loginService;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 登录接口
     */
    @PostMapping("/adminLogin")
    public Result login(@RequestBody LoginDTO loginDTO) {
        log.info("管理员登录登录：{}", loginDTO.getUsername());
        return loginService.adminLogin(loginDTO);
    }

    @PostMapping("/loginWithCode")
    public Result loginWithCode(@RequestBody LoginDTO loginDTO) {
        log.info("用户开始登录(通过验证码)：{},邮箱：{}", loginDTO.getCode(),loginDTO.getEmail());
        return loginService.loginWithCode(loginDTO);
    }

    @PostMapping("/loginWithEmail")
    public Result loginWithEmail(@RequestBody LoginDTO loginDTO) {
        log.info("用户开始登录(通过邮箱)：{},邮箱：{}", loginDTO.getUsername(),loginDTO.getEmail());
        return loginService.loginWithEmail(loginDTO);
    }

    @PostMapping("/sendEmailCode")
    public Result sendEmailCode(@RequestParam String email) throws MessagingException {
        if (email == null || email.isEmpty()){
            return Result.error("邮箱不能为空");
        }
        if(redisUtils.checkSMSCodeSendOverTimes(SMS_CODE_TIMES_KEY+email,SMS_CODE_TIMES_LIMIT,CODE_EXPIRE_TIME)){
            return Result.error("验证码发送过于频繁，请稍后再试");
        }
        log.info("用户开始发送邮箱验证码：{}", email);
        String code = CodeUtil.generateSixCode();
        log.info("生成的验证码：{}",code);
        redisUtils.setStringValue(EMAIL_KEY+email, code, CODE_EXPIRE_TIME);
        redisUtils.increaseSMSCodeSendTimes(SMS_CODE_TIMES_KEY+email);
        log.info("已将验证码保存到redis中：{}",code);
        //emailUtil.sendVerifyCodeHtml(email, code);
        return Result.success();
    }

    @PostMapping("/detLogin")
    public Result detLogin() {
        log.info("检测用户是否已经登录");
        return Result.success();
    }

    @PostMapping("/logout")
    public Result logout() {
        LoginResult loginResult = CurrentHolder.getCurrentUserInfo();
        log.info("用户开始登出：{}",loginResult.getId());
        return loginService.logout(loginResult);
    }
}