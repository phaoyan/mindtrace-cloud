package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.handler.FeignSecurityInterceptor;

@Service
@FeignClient(name = "mindtrace-gateway", configuration = FeignSecurityInterceptor.class)
public interface GlobalClient {

    // mindtrace-gateway
    @GetMapping("/user/{id}")
    SaResult userExists(@PathVariable Long id);
    // mindtrace-core
    @GetMapping("/core/user/{userId}/knode/label")
    SaResult checkKnodeByLabel(@PathVariable Long userId, @RequestParam("labelName") String labelName);
    @GetMapping("/core/user/{userId}/knode/{knodeId}/leaves")
    SaResult getKnodeLeaves(@PathVariable Long userId, @PathVariable Long knodeId);

    // mindtrace-enhancer
    @GetMapping("/enhancer/user/{userId}/enhancer/label")
    SaResult checkEnhancerByLabel(@PathVariable Long userId,@RequestParam("labelName") String labelName);
}


