package pers.juumii.aop;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceAspect {


    @Pointcut("execution(void pers.juumii.service.impl.serializer.* (pers.juumii.data.Resource, java.util.Map))")
    void resourceSerializer(){}

}
