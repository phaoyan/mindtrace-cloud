package pers.juumii.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtils {

    public static <T> Object performanceMonitor(ProceedingJoinPoint joinPoint, Class<T> cl) throws Throwable {
        Logger logger = LoggerFactory.getLogger(cl);
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
