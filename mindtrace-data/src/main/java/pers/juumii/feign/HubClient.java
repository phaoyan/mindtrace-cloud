package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.hub.MetadataDTO;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

import java.util.Map;

@FeignClient(
        contextId = "mindtrace-hub",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface HubClient {


    @PutMapping("/hub/user/{userId}")
    MetadataDTO push(@PathVariable Long userId, @RequestBody String base64);

    @PostMapping("/hub/user/{userId}/resource/{resourceId}")
    void setMeta(@PathVariable Long userId, @PathVariable Long resourceId, @RequestBody Map<String, Object> meta);

    @GetMapping("/hub/user/{userId}/resource/{resourceId}")
    Map<String, Object> getMeta(@PathVariable Long userId, @PathVariable Long resourceId);

    @GetMapping("/hub/hello")
    String hello();
}
