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

    @Around("global()")
    public Object globalActs(ProceedingJoinPoint joinPoint) throws Throwable{
        try {
            Object res = joinPoint.proceed();
            // 如果controller使用的service不返回SaResult，则将其包装为SaResult
            if(res instanceof SaResult) return res;
            else return SaResult.data(res);
            // 服务类的异常在此处捕获，通过SaResult.error返回
        } catch (RuntimeException e){
            return SaResult.error(e.getMessage());
        }
    }

    public void checkUserExistence(Long userId){
        if(!Convert.toBool(client.userExists(userId).getData()))
            throw new RuntimeException("User not found: " + userId);
    }

    public void checkTraceExistent(Long traceId){

    }

    public void checkTraceAvailability(Long userId, Long traceId){

    }

    public void checkKnodeAvailability(Long userId, Long knodeId) {

    }

    public void checkEnhancerAvailability(Long userId, Long enhancerId) {

    }
}
