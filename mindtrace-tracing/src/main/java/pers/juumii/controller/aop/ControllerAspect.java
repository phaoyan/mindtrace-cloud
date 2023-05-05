package pers.juumii.controller.aop;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(* pers.juumii.controller.*.* (..))")
    public void global(){}

    @Around("global()")
    public Object performanceMonitor(ProceedingJoinPoint joinPoint) throws Throwable {
        TimeInterval timer = DateUtil.timer();
        Object res = joinPoint.proceed();
        System.out.println(joinPoint.getSignature() + " --- " + timer.interval());
        return res;
    }

}
