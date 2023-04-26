package pers.juumii.controller.aop;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.feign.GatewayClient;
import pers.juumii.mapper.LearningTraceMapper;

@Aspect
@Component
public class ControllerAspect {

    private final LearningTraceMapper learningTraceMapper;
    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final GatewayClient gatewayClient;

    @Autowired
    public ControllerAspect(
            LearningTraceMapper learningTraceMapper,
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            GatewayClient gatewayClient) {
        this.learningTraceMapper = learningTraceMapper;
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.gatewayClient = gatewayClient;
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
        if(!Convert.toBool(gatewayClient.userExists(userId).getData()))
            throw new RuntimeException("User not found: " + userId);
    }

    public void checkTraceExistence(Long traceId){
        if(learningTraceMapper.selectById(traceId) == null)
            throw new RuntimeException("Trace not found: " + traceId);

    }

    public void checkTraceAvailability(Long userId, Long traceId){
        checkUserExistence(userId);
        checkTraceExistence(traceId);
        if(!learningTraceMapper.selectById(traceId).getCreateBy().equals(userId))
            throw new RuntimeException("Trace not available: " + traceId + " for user " + userId);
    }

    public void checkKnodeAvailability(Long userId, Long knodeId) {
        if(coreClient.checkKnode(userId, knodeId).getData() == null)
            throw new RuntimeException("Knode not available: " + knodeId + " for user " + userId);
    }

    public void checkEnhancerAvailability(Long userId, Long enhancerId) {
        if(enhancerClient.getEnhancerFromUser(userId, enhancerId).getData() == null)
            throw new RuntimeException("Enhancer not available: " + enhancerId + " for user " + userId);
    }
}
