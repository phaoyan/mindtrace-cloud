package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.handler.FeignSecurityInterceptor;

import java.awt.color.ICC_Profile;

@Service
@FeignClient(name = "mindtrace-tracing", configuration = FeignSecurityInterceptor.class)
public interface GlobalClient {

    // mindtrace-gateway
    @GetMapping("/user/{id}")
    SaResult userExists(@PathVariable Long id);


    // mindtrace-core
    @GetMapping("/core/user/{userId}/knode/label")
    SaResult checkKnodeByLabel(@PathVariable Long userId, @RequestParam("labelName") String labelName);

    // mindtrace-enhancer
    @GetMapping("/enhancer/user/{userId}/enhancer/label")
    SaResult checkEnhancerByLabel(@PathVariable Long userId,@RequestParam("labelName") String labelName);
}


