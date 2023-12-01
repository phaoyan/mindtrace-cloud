package pers.juumii.controller.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import pers.juumii.utils.LoggingUtils;

@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(* pers.juumii.controller.*.* (..))")
    public void global(){}

    @Around("global()")
    public Object performanceMonitor(ProceedingJoinPoint joinPoint) throws Throwable {
        return LoggingUtils.performanceMonitor(joinPoint, getClass());
    }
}