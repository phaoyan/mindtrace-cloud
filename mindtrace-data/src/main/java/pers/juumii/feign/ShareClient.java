package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-share",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface ShareClient {

    @GetMapping("/share/user/{userId}/public")
    Boolean isUserPublic(@PathVariable Long userId);
    @GetMapping("/share/knode/{knodeId}/public")
    Boolean isKnodePublic(@PathVariable Long knodeId);
    @GetMapping("/share/enhancer/{enhancerId}/public")
    Boolean isEnhancerPublic(@PathVariable Long enhancerId);
    @GetMapping("/share/resource/{resourceId}/public")
    Boolean isResourcePublic(@PathVariable Long resourceId);

}
