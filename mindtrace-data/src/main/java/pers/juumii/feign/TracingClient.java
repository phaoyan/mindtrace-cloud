package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

import java.util.List;

@FeignClient(
        contextId = "mindtrace-tracing",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface TracingClient {
}
