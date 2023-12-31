package pers.juumii.feign;

import cn.dev33.satoken.util.SaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.*;
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
    @PostMapping("/enhancer/batch/knode/enhancer")
    List<EnhancerDTO> getEnhancerOfKnodeBatch(@RequestBody List<Long> knodeIds);
    @GetMapping("/enhancer/knode/{knodeId}/offspring/enhancer")
    List<EnhancerDTO> getEnhancersFromKnodeIncludingBeneath(@PathVariable Long knodeId);
    @GetMapping("/enhancer/knode/{knodeId}/offspring/enhancer/id")
    List<Long> getEnhancerIdsFromKnodeIncludingBeneath(@PathVariable Long knodeId);
    @GetMapping("/enhancer/enhancer/{enhancerId}/resource")
    List<ResourceDTO> getResourcesOfEnhancer(@PathVariable Long enhancerId);
    @PostMapping("/enhancer/batch/enhancer/resource")
    List<ResourceDTO> getResourcesOfEnhancerBatch(@RequestBody List<Long> enhancerIds);
    @GetMapping("/enhancer/knode/{knodeId}/resource")
    List<ResourceDTO> getResourcesOfKnode(@PathVariable Long knodeId);
    @GetMapping("/enhancer/resource/{resourceId}/meta")
    ResourceDTO getResourceMetadata(@PathVariable Long resourceId);
    @GetMapping("/enhancer/resource/{resourceId}/data")
    Map<String, byte[]> getDataFromResource(@PathVariable Long resourceId);
    @GetMapping("/enhancer/resource/{resourceId}/data/{dataName}")
    byte[] getDataFromResource(@PathVariable Long resourceId, @PathVariable String dataName);
    @PostMapping("/enhancer/batch/resource/data")
    List<Map<String, byte[]>> getDataFromResourceBatch(@RequestBody List<Long> resourceIds);
    @PutMapping("/enhancer/resource/{resourceId}/data")
    SaResult addDataToResource(@PathVariable Long resourceId, @RequestBody Map<String, byte[]> data);
    @PutMapping("/enhancer/knode/{knodeId}/enhancer")
    EnhancerDTO addEnhancerToKnode(@PathVariable Long knodeId);
    @PutMapping("/enhancer/enhancer/{enhancerId}/resource")
    ResourceDTO addResourceToEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody ResourceWithData params);
    @PostMapping("/enhancer/enhancer/{enhancerId}")
    SaResult updateEnhancer(
            @PathVariable Long enhancerId,
            @RequestBody EnhancerDTO updated);
    @GetMapping("/enhancer/enhancer/{enhancerId}/knode")
    List<KnodeDTO> getKnodeByEnhancerId(@PathVariable Long enhancerId);
    @GetMapping("/enhancer/knode/{rootId}/withQuiz")
    List<String> getKnodeIdsWithQuiz(@PathVariable Long rootId);
    @PostMapping("/enhancer/rel/knode/enhancer")
    List<IdPair> getKnodeEnhancerRels(@RequestBody List<Long> knodeIds);
    @PostMapping("/enhancer/rel/enhancer/resource")
    List<IdPair> getEnhancerResourceRels(@RequestBody List<Long> enhancerId);
    @PutMapping("/enhancer/enhancer")
    EnhancerDTO addEnhancer();
    @PutMapping("/enhancer/rel/knode/enhancer")
    void addKnodeEnhancerRel(@RequestParam Long knodeId, @RequestParam Long enhancerId);
    @PutMapping("/enhancer/rel/enhancer/resource")
    void addEnhancerResourceRel(@RequestParam Long enhancerId, @RequestParam Long resourceId);
    @PutMapping("/enhancer/resource")
    ResourceDTO addResource(@RequestParam String type);
    @PutMapping("/enhancer/resource/{resourceId}/title")
    void resourceEditTitle(@PathVariable Long resourceId, @RequestParam String title);
    @PutMapping("/enhancer/resource/{resourceId}/type")
    void resourceEditType(@PathVariable Long resourceId, @RequestParam String type);
    @PutMapping("/enhancer/resource/{resourceId}/createTime")
    void resourceEditCreateTime(@PathVariable Long resourceId, @RequestParam String createTime);
    @GetMapping("/enhancer/resource/{resourceId}/meta")
    ResourceDTO getResourceById(@PathVariable Long resourceId);
    @DeleteMapping("/enhancer/resource/{resourceId}")
    void removeResourceById(@PathVariable Long resourceId);
    @GetMapping("/enhancer/resource/{resourceId}/data/{dataName}/url")
    String getCosResourceUrl(@PathVariable Long resourceId, @PathVariable String dataName);
}
