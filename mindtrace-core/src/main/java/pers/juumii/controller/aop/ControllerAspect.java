package pers.juumii.controller.aop;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.juumii.feign.UserClient;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.UserService;

@Aspect
@Component
public class ControllerAspect {

    private final UserClient userClient;
    private final UserRepository userRepo;
    private final UserService userService;
    private final KnodeRepository knodeRepo;


    @Autowired
    public ControllerAspect(
            UserClient userClient,
            UserRepository userRepo,
            UserService userService,
            KnodeRepository knodeRepo) {
        this.userClient = userClient;
        this.userRepo = userRepo;
        this.userService = userService;
        this.knodeRepo = knodeRepo;
    }

    @Pointcut("execution(Object pers.juumii.controller.*.* (..))")
    public void global(){}

    @Pointcut(value = "execution(Object pers.juumii.controller.KnodeController.* (Long,Long,..)) && args(userId,knodeId,..)", argNames = "userId,knodeId")
    public void knodeController(Long userId, Long knodeId){}

    @Pointcut(value = "execution(Object pers.juumii.controller.KnodeQueryController.* (Long, Long)) && args(userId, knodeId)", argNames = "userId,knodeId")
    public void knodeQueryController(Long userId, Long knodeId){}

    @Pointcut("execution(Object pers.juumii.controller.UserController.* (Long,..)) && args(userId,..) && " +
              "!execution(Object pers.juumii.controller.UserController.register(Long))")
    public void userController(Long userId){}

    @Around("global()")
    public Object wrapResult(ProceedingJoinPoint joinPoint) throws Throwable {
        // 如果controller使用的service不返回SaResult，则将其包装为SaResult
        Object res = joinPoint.proceed();
        if(res instanceof SaResult) return res;
        else return SaResult.data(res);
    }

    @Order(0)
    @Around(value = "userController(userId)", argNames = "joinPoint,userId")
    public Object checkUser(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        // 用户不存在
        if(!userExists(userId))
            return SaResult.error("User not found: " + userId);
        // 用户未注册到Neo4J中
        if(!userRegistered(userId))
            return SaResult.error("User not registered: "  + userId);
        return joinPoint.proceed();
    }

    @Around(value = "knodeQueryController(userId, knodeId) || knodeController(userId, knodeId)", argNames = "joinPoint,userId,knodeId")
    public Object checkUser(ProceedingJoinPoint joinPoint, Long userId, Long knodeId) throws Throwable {
        return checkUser(joinPoint, userId);
    }

    @Around(value = "knodeQueryController(userId, knodeId) || knodeController(userId, knodeId)", argNames = "joinPoint,userId,knodeId")
    public Object checkKnode(ProceedingJoinPoint joinPoint, Long userId, Long knodeId) throws Throwable {
        // 节点不存在
        if(!knodeRepo.existsById(knodeId))
            return SaResult.error(StrUtil.format("Knode not found: " + knodeId));
        // 用户并不拥有该节点
        if(!userService.possesses(userId, knodeId))
            return SaResult.error(StrUtil.format("Authentication failed: user {} does not possess knode {}", userId, knodeId));
        return joinPoint.proceed();
    }

    private Boolean userRegistered(Long userId) {
        return userRepo.existsById(userId);
    }

    private Boolean userExists(Long userId) {
        return Convert.toBool(userClient.userExists(userId).getData());
    }

}
