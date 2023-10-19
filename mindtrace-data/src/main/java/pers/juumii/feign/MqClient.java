package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-mq",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface MqClient {

    @PutMapping("/mq/event")
    void emit(@RequestParam String event, @RequestParam String data);

    @PutMapping("/mq/listener")
    void addListener(@RequestParam String event, @RequestParam String callback);

}
