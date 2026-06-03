package com.exchange.aop;

import com.exchange.Utils.CurrentHolder;
import com.exchange.mapper.OperateLogMapper;
import com.exchange.pojo.OperateLog;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component // 将切面类交给Spring容器管理
public class OperationLogAspect {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Around("@annotation(com.exchange.anno.Log)")
    public Object LogOperation(ProceedingJoinPoint pjp) throws Throwable {
        long startTime=System.currentTimeMillis();
        //执行目标方法
        Object result=pjp.proceed();
        long endTime=System.currentTimeMillis();
        long costTime=endTime-startTime; // 执行时长
        OperateLog operateLog=new OperateLog();
        operateLog.setOperateEmpId(getCurrentUserId());
        operateLog.setOperateTime(LocalDateTime.now());
        operateLog.setClassName(pjp.getTarget().getClass().getName());
        operateLog.setMethodName(pjp.getSignature().getName());
        operateLog.setMethodParams(Arrays.toString(pjp.getArgs()));
        operateLog.setReturnValue(result != null ? result.toString():"void");
        operateLog.setCostTime(costTime);
        operateLogMapper.insert(operateLog);
        return result;
    }
    private Long getCurrentUserId(){
        return CurrentHolder.getCurrentUserInfo().getId();
    }
}
