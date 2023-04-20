package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pers.juumii.handler.FeignSecurityInterceptor;

import java.util.Map;

@FeignClient(name = "mindtrace-gateway", configuration = FeignSecurityInterceptor.class)
public interface GlobalClient {

    @GetMapping("/user/{id}")
    SaResult userExists(@PathVariable Long id);

}
