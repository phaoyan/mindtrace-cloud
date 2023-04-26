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
import pers.juumii.feign.GatewayClient;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.ResourceMapper;

@Aspect
@Component
public class ControllerAspect {

    private final GatewayClient gatewayClient;
    private final CoreClient coreClient;
    private final EnhancerMapper enhancerMapper;
    private final ResourceMapper resourceMapper;

    @Autowired
    public ControllerAspect(
            GatewayClient gatewayClient,
            CoreClient coreClient,
            EnhancerMapper enhancerMapper,
            ResourceMapper resourceMapper) {
        this.gatewayClient = gatewayClient;
        this.coreClient = coreClient;
        this.enhancerMapper = enhancerMapper;
        this.resourceMapper = resourceMapper;
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

    public void checkUserExistence(Long userId) {
        if(!Convert.toBool(gatewayClient.userExists(userId).getData()))
            throw new RuntimeException("User not found : " + userId);
    }

    public void checkEnhancerExistence(Long enhancerId){
        if(enhancerMapper.selectById(enhancerId) == null)
            throw new RuntimeException("Enhancer not found: " + enhancerId);
    }

    public void checkKnodeAvailability(Long userId, Long knodeId){
        if(coreClient.checkKnode(userId, knodeId).getData() == null)
            throw new RuntimeException("Knode not available: " + knodeId + " for user " + userId);
    }

    public void checkEnhancerAvailability(Long userId, Long enhancerId){
        checkUserExistence(userId);
        checkEnhancerExistence(enhancerId);
        if(!enhancerMapper.selectById(enhancerId).getCreateBy().equals(userId))
            throw new RuntimeException("Enhancer not available: " + enhancerId + " for user " + userId);
    }

    public void checkResourceExistence(Long resourceId){
        if(resourceMapper.selectById(resourceId) == null)
            throw new RuntimeException("Resource not found: " + resourceId);
    }

    public void checkResourceAvailability(Long userId, Long resourceId){
        checkUserExistence(userId);
        checkResourceExistence(resourceId);
        if(!resourceMapper.selectById(resourceId).getCreateBy().equals(userId))
            throw new RuntimeException("Resource not available: " + resourceId + " for user " + userId);
    }

}
