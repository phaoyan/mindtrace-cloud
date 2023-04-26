package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

@FeignClient(
        contextId = "mindtrace-tracing",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface TracingClient {
}
