package pers.juumii.controller.aop;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.TypeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.data.Knode;
import pers.juumii.feign.UserClient;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.UserService;

import java.util.List;

@Aspect
@Component
public class ControllerAspect {

    private final UserClient userClient;
    private final UserService userService;
    private final KnodeRepository knodeRepo;


    @Autowired
    public ControllerAspect(
            UserClient userClient,
            UserService userService,
            KnodeRepository knodeRepo) {
        this.userClient = userClient;
        this.userService = userService;
        this.knodeRepo = knodeRepo;
    }

    @Pointcut("execution(Object pers.juumii.controller.*.* (..))")
    public void global(){}

    @Pointcut("execution(Object pers.juumii.controller.KnodeQueryController.* (..))")
    public void knodeQuery(){}

    @Around("global()")
    public Object wrapResult(ProceedingJoinPoint joinPoint) throws Throwable {
        // 如果controller使用的service不返回SaResult，则将其包装为SaResult
        Object res = joinPoint.proceed();
        if(res instanceof SaResult) return res;
        else return SaResult.data(res);
    }

    // 服务类异常统一在此处理
    @Around("global()")
    public Object handleException(ProceedingJoinPoint joinPoint){
        try{
            return joinPoint.proceed();
        }catch (Throwable e){
            return SaResult.error(e.getMessage());
        }
    }

    public void checkUserExistence(Long userId) {
        if(!Convert.toBool(userClient.userExists(userId).getData()))
            throw new RuntimeException("User not found: " + userId);
    }

    public void checkKnodeExistence(Long knodeId) {
        if(!knodeRepo.existsById(knodeId))
            throw new RuntimeException("Knode not found: " + knodeId);
    }

    public void checkKnodeAvailability(Long userId, Long knodeId) {
        checkUserExistence(userId);
        checkKnodeExistence(knodeId);
        if(!userService.findUserId(knodeId).equals(userId))
            throw new RuntimeException("Knode not available: " + knodeId + " for user " + userId);
    }
}
