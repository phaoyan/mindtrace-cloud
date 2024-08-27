package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;


@FeignClient(
        contextId = "mindtrace-chat",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface ChatClient {

    @PostMapping("/chat/chat")
    String getResponse(@RequestBody String json);

    @PostMapping("/chat/chat/stream")
    String getResponseStream(@RequestBody String json);
}
