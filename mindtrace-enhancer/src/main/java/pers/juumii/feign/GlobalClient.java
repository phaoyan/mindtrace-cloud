package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.juumii.handler.FeignSecurityInterceptor;

@FeignClient(name = "mindtrace-gateway", configuration = FeignSecurityInterceptor.class)
public interface GlobalClient {

    @GetMapping("/user/{id}")
    SaResult userExists(@PathVariable Long id);

    @GetMapping("/core/knode/{id}")
    SaResult checkKnode(@PathVariable Long id);

    @GetMapping("/core/hello")
    SaResult echo();

}
