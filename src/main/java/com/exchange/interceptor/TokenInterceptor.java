package com.exchange.interceptor;

import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.JWTUtils;
import com.exchange.dto.LoginResult;
import com.exchange.dto.LoginToken;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.exchange.constants.SystemConstants.*;


@Slf4j
@Component // 将类标记为组件，自动注入到spring容器中
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtils redisUtils;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String idStr = request.getHeader("id");
        String username = request.getHeader("username");
        if(idStr==null || idStr.isEmpty())
        {
            log.info("用户未登录");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Long id = Long.parseLong(idStr);
        String schoolIdStr = request.getHeader("school");
        Long schoolId = null;
        if (schoolIdStr!=null&&!schoolIdStr.isEmpty()){
            schoolId = Long.parseLong(schoolIdStr);
        }
        String isBanded = redisUtils.getStringValue(ACCOUNT_BANDED_KEY + id, String.class);
        if(isBanded!=null)
        {
            log.info("用户被封禁");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            redisUtils.remove(ACCOUNT_BANDED_KEY + id);
            return false;
        }
        int role = request.getIntHeader("role");
        LoginToken loginToken;
        log.info("查询Redis获取Token用户ID：{}", id);
        loginToken = redisUtils.getStringValue(TOKEN_KEY+id, LoginToken.class);
        if(token==null || token.isEmpty())
        {
            log.info("用户未登录");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        if(loginToken!=null&&loginToken.getToken().equals(token)){
            try{
                JWTUtils.parseToken(token);
            } catch (Exception e) {
                log.info("token无效,跳转到登录界面");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }else {
            log.info("token与redis不匹配,跳转到登录界面");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //校验通过，放行
        CurrentHolder.setCurrentUser(new LoginResult(id, username, token, role, schoolId, null));
        log.info("刷新Token时效:");
        if (role==2){
            redisUtils.setStringValue(TOKEN_KEY+id, loginToken, TOKEN_EXPIRE_TIME);
        }else {
            redisUtils.setStringValue(TOKEN_KEY+id, loginToken, USER_TOKEN_EXPIRE_TIME);
        }
        log.info("校验通过，放行");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentHolder.remove();
    }

}
