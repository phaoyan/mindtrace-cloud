package pers.juumii.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * 用于将service路由到gateway的相应网关，具体而言是在header里面加一个x-service-name
 */
@Component
public class ServiceNameFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        if(requestTemplate.feignTarget() != null){
            Class<?> feignClientClass = requestTemplate.feignTarget().type();
            FeignClient feignClientAnnotation = feignClientClass.getAnnotation(FeignClient.class);
            if (feignClientAnnotation != null) {
                String serviceName = feignClientAnnotation.name();
                requestTemplate.header("x-service-name", serviceName);
            }
        }
    }
}
