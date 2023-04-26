package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-enhancer",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface EnhancerClient {

    @GetMapping("/enhancer/user/{userId}/enhancer/label")
    SaResult checkEnhancerByLabel(@PathVariable Long userId, @RequestParam("labelName") String labelName);
    @GetMapping("/enhancer/user/{userId}/enhancer/{enhancerId}")
    SaResult getEnhancerFromUser(@PathVariable Long userId, @PathVariable Long enhancerId);
}
