package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

import java.util.List;
import java.util.Map;

@FeignClient(
        contextId = "mindtrace-core",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface CoreClient {

    @GetMapping("/knode")
    List<KnodeDTO> checkAll(@RequestParam String password);
    @GetMapping("/core/knode/{knodeId}")
    KnodeDTO check(@PathVariable Long knodeId);
    @PostMapping("/core/batch/knode")
    List<KnodeDTO> checkBatch(@RequestBody List<Long> knodeIds);
    @PostMapping("/core/batch/knode/ancestor/id")
    Map<Long, List<Long>> ancestorIdsBatch(@RequestBody List<Long> knodeIds);
    @GetMapping("/core/knode/{knodeId}/leaves")
    List<KnodeDTO> leaves(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/createBy")
    Long checkKnodeCreateBy(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/offsprings")
    List<KnodeDTO> offsprings(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/offspring/id")
    List<Long> offspringIds(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/chainStyleTitle")
    List<String> chainStyleTitle(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/chainStyleTitleBeneath")
    Map<String,List<String>> chainStyleTitleBeneath(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/ancestor")
    List<KnodeDTO> ancestors(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/stem")
    KnodeDTO stem(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{parentId}/knode/{childId}")
    Boolean isOffspring(@PathVariable Long childId, @PathVariable Long parentId);
    @GetMapping("/core/hello")
    Object hello();
    @PostMapping("/core/knode/{stemId}/branch")
    KnodeDTO branch(@PathVariable Long stemId, @RequestParam String title);
    @PostMapping("/core/knode/{knodeId}/createTime")
    void editCreateTime(@PathVariable Long knodeId, @RequestParam String createTime);
    @PostMapping("/core/knode/{knodeId}/createBy")
    void editCreateBy(@PathVariable Long knodeId, @RequestParam String createBy);
    @PostMapping("/core/knode/{knodeId}/title")
    void editTitle(@PathVariable Long knodeId, @RequestParam String title);
    @PostMapping("/core/knode/{knodeId}/index")
    void editIndex(@PathVariable Long knodeId, @RequestParam Integer index);

}
