package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-hub",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface HubClient {


    @PutMapping("/hub/user/{userId}")
    void push(@PathVariable Long userId, @RequestBody String base64);


    @GetMapping("/hub/hello")
    String hello();
}
