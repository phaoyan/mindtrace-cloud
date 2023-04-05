package pers.juumii.controller.aop;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.feign.GlobalClient;

@Aspect
@Component
public class ControllerAspect {

    private final GlobalClient client;

    @Autowired
    public ControllerAspect(GlobalClient client) {
        this.client = client;
    }

    @Pointcut("execution(Object pers.juumii.controller.*.* (..))")
    public void global(){}

    @Pointcut("execution(Object pers.juumii.controller.*.* (Long,..)) && args(userId)")
    public void userRequirement(Long userId){}

    @Around("global()")
    public Object wrapResult(ProceedingJoinPoint joinPoint) throws Throwable{
        // 如果controller使用的service不返回SaResult，则将其包装为SaResult
        Object res = joinPoint.proceed();
        if(res instanceof SaResult) return res;
        else return SaResult.data(res);
    }

    @Around(value = "userRequirement(userId)", argNames = "joinPoint,userId")
    public Object checkUser(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        if(!Convert.toBool(client.userExists(userId).getData()))
            return SaResult.error("User not found: " + userId);
        return joinPoint.proceed();
    }
}
