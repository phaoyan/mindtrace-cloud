package pers.juumii.service.impl.aop;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.Area;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.feign.UserClient;
import pers.juumii.utils.SaResult;


@Aspect
@Component
public class ServiceAspect {

    private final UserClient userClient;

    @Autowired
    public ServiceAspect(UserClient userClient) {
        this.userClient = userClient;
    }


    @Pointcut("@annotation(pers.juumii.annotation.CheckUserExistence) && args(userId)")
    public void checkUserPointCut(Long userId){}

    @Around(value = "checkUserPointCut(userId)", argNames = "joinPoint,userId")
    public Object checkUserExistence(
            ProceedingJoinPoint joinPoint,
            Long userId) throws Throwable {
        if(!userClient.userExists(userId))
            return SaResult.error("User not found: " + userId);
        return joinPoint.proceed();
    }
}
