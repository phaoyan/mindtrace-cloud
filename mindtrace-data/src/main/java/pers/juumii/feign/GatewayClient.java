package pers.juumii.feign;


import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(name = "mindtrace-gateway", configuration = FeignSecurityInterceptor.class)
public interface GatewayClient {

    @GetMapping("/user/{id}/exists")
    SaResult userExists(@PathVariable Long id);

}