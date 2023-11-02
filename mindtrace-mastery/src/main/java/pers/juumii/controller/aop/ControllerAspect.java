package pers.juumii.controller.aop;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    @Pointcut("execution(* pers.juumii.controller.*.* (..))")
    public void global(){}

    @Around("global()")
    public Object performanceMonitor(ProceedingJoinPoint joinPoint) throws Throwable {
        TimeInterval timer = DateUtil.timer();
        Object res = joinPoint.proceed();
        long interval = timer.interval();
        if(interval < 1000)
            logger.info(joinPoint.getSignature() + " --- " + interval);
        else
            logger.warn(joinPoint.getSignature() + " --- " + interval + " !!! LOW SPEED !!!");
        return res;
    }
}
