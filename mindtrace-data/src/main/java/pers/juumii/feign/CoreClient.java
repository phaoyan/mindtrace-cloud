package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-core",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface CoreClient {

    // mindtrace-core
    @GetMapping("/core/user/{userId}/knode/{knodeId}")
    SaResult checkKnode(@PathVariable Long userId, @PathVariable Long knodeId);
    @GetMapping("/core/user/{userId}/knode/label")
    SaResult checkKnodeByLabel(@PathVariable Long userId, @RequestParam("labelName") String labelName);
    @GetMapping("/core/user/{userId}/knode/{knodeId}/leaves")
    SaResult getKnodeLeaves(@PathVariable Long userId, @PathVariable Long knodeId);


}
