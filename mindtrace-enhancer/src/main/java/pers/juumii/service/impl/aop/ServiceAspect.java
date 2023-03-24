package pers.juumii.service.impl.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.feign.UserClient;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.utils.SaResult;

import java.util.Objects;

@Aspect
@Component
public class ServiceAspect {

    private final UserClient userClient;
    private final EnhancerMapper enhancerMapper;
    private final LabelMapper labelMapper;
    private final ResourceMapper resourceMapper;

    @Autowired
    public ServiceAspect(
            UserClient userClient,
            EnhancerMapper enhancerMapper,
            LabelMapper labelMapper,
            ResourceMapper resourceMapper) {
        this.userClient = userClient;
        this.enhancerMapper = enhancerMapper;
        this.labelMapper = labelMapper;
        this.resourceMapper = resourceMapper;
    }

    @Pointcut("@annotation(pers.juumii.annotation.CheckUserExistence) && args(userId)")
    public void checkUserPointCut(Long userId){}

    @Pointcut("@annotation(pers.juumii.annotation.CheckEnhancerExistence) && args(enhancerId)")
    public void checkEnhancerPointCut(Long enhancerId){}

    @Pointcut("@annotation(pers.juumii.annotation.CheckLabelExistence) && args(labelName)")
    public void checkLabelPointCut(String labelName){}

    @Pointcut("@annotation(pers.juumii.annotation.CheckResourceExistence) && args(resourceId)")
    public void checkResourcePointCut(Long resourceId){}

    @Around(value = "checkUserPointCut(userId)", argNames = "joinPoint,userId")
    public Object checkUserExistence(
            ProceedingJoinPoint joinPoint,
            Long userId) throws Throwable {
        if(!userClient.userExists(userId))
            return SaResult.error("User not found: " + userId);
        return joinPoint.proceed();
    }

    @Around(value = "checkEnhancerPointCut(enhancerId)", argNames = "joinPoint,enhancerId")
    public Object checkEnhancerExistence(
            ProceedingJoinPoint joinPoint,
            Long enhancerId) throws Throwable {
        if(Objects.isNull(enhancerMapper.selectById(enhancerId)))
            return SaResult.error("Enhancer not found: " + enhancerId);
        return joinPoint.proceed();
    }

    @Around(value = "checkLabelPointCut(labelName)", argNames = "joinPoint,labelName")
    public Object checkLabelExistence(
            ProceedingJoinPoint joinPoint,
            String labelName) throws Throwable {
        if(Objects.isNull(labelMapper.selectById(labelName)))
            return SaResult.error("Label not found: " + labelName);
        return joinPoint.proceed();
    }

    @Around(value = "checkResourcePointCut(resourceId)", argNames = "joinPoint,resourceId")
    public Object checkResourceExistence(
            ProceedingJoinPoint joinPoint,
            Long resourceId) throws Throwable {
        if(Objects.isNull(resourceMapper.selectById(resourceId)))
            return SaResult.error("Resource not found: " + resourceId);
        return joinPoint.proceed();
    }
}
