package pers.juumii.controller.aop;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.data.Knode;
import pers.juumii.feign.GatewayClient;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.UserService;


@Aspect
@Component
public class ControllerAspect {

    private final GatewayClient gatewayClient;
    private final UserService userService;
    private final KnodeRepository knodeRepo;
    private final KnodeQueryService knodeQuery;

    @Autowired
    public ControllerAspect(
            GatewayClient gatewayClient,
            UserService userService,
            KnodeRepository knodeRepo, KnodeQueryService knodeQuery) {
        this.gatewayClient = gatewayClient;
        this.userService = userService;
        this.knodeRepo = knodeRepo;
        this.knodeQuery = knodeQuery;
    }

    @Pointcut("execution(Object pers.juumii.controller.*.* (..))")
    public void global(){}

    @Around("global()")
    public Object wrapResult(ProceedingJoinPoint joinPoint) throws Throwable {
        TimeInterval timer = DateUtil.timer();

        // 如果controller使用的service不返回SaResult，则将其包装为SaResult
        Object res = joinPoint.proceed();

        System.out.println(joinPoint.getSignature() + " --- " + timer.interval());
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
        if(!Convert.toBool(gatewayClient.userExists(userId).getData()))
            throw new RuntimeException("User not found: " + userId);
    }

    public void checkKnodeAvailability(Long userId, Long knodeId) {
        Knode check = knodeQuery.check(knodeId);
        if(check == null || !check.getCreateBy().equals(userId))
            throw new RuntimeException("Knode not available: " + knodeId + " for user " + userId);
    }
}
