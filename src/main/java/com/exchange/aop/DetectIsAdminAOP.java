package com.exchange.aop;

import com.exchange.Utils.DetectIsAdmin;
import com.exchange.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class DetectIsAdminAOP {
    @Around("@annotation(com.exchange.anno.RequiredAdmin)")
    public Object checkAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!DetectIsAdmin.isAdmin()) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setContentType("application/json;charset=utf-8");
            Result result = Result.error("无管理员权限");
            new ObjectMapper().writeValue(response.getWriter(), result);
            return null;
        }
        return joinPoint.proceed();
    }
}
