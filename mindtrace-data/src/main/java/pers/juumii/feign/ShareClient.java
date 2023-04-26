package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-share",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface ShareClient {
}
