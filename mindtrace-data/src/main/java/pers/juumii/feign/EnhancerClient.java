package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.ResourceWithData;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

import java.util.List;
import java.util.Map;

@FeignClient(
        contextId = "mindtrace-enhancer",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface EnhancerClient {

    @GetMapping("/enhancer/enhancer/{enhancerId}")
    EnhancerDTO getEnhancerById(@PathVariable Long enhancerId);

    @GetMapping("/enhancer/knode/{knodeId}/enhancer")
    List<EnhancerDTO> getEnhancersOfKnode(@PathVariable Long knodeId);

    @GetMapping("/enhancer/enhancer/{enhancerId}/resource")
    List<ResourceDTO> getResourcesOfEnhancer(@PathVariable Long enhancerId);

    @GetMapping("/enhancer/resource/{resourceId}/data")
    Map<String, Object> getDataFromResource(@PathVariable Long resourceId);

    @PutMapping("/enhancer/knode/{knodeId}/enhancer")
    EnhancerDTO addEnhancerToKnode(@PathVariable Long knodeId);

    @PutMapping("/enhancer/enhancer/{enhancerId}/resource")
    ResourceDTO addResourceToEnhancer(@PathVariable Long enhancerId, @RequestBody ResourceWithData params);

    @PostMapping("/enhancer/enhancer/{enhancerId}")
    SaResult updateEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody EnhancerDTO updated);
}
