package pers.juumii.controller.aop;

import cn.dev33.satoken.util.SaResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

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
}
