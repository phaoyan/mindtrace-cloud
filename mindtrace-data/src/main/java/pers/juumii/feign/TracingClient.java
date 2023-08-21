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

    @GetMapping("/tracing/study/trace")
    List<StudyTraceDTO> getUserStudyTraces(@RequestParam(value = "userId", required = false) Long userId);

    @PostMapping("/tracing/batch/study/knode/trace")
    List<StudyTraceDTO> getStudyTracesOfKnodeBatch(@RequestBody List<Long> knodeIds);

    @PostMapping("/tracing/rel/trace/knode")
    List<IdPair> getTraceKnodeRels(@RequestBody List<Long> traceIds);

    @PostMapping("/tracing/rel/trace/enhancer")
    List<IdPair> getTraceEnhancerRels(@RequestBody List<Long> traceIds);
    @PutMapping("/tracing/study/trace")
    StudyTraceDTO addStudyTrace(@RequestBody StudyTraceDTO trace);
    @PutMapping("/tracing/rel/trace/knode")
    void addStudyTraceKnodeRel(@RequestBody IdPair traceKnodeRel);
    @PutMapping("/tracing/rel/trace/enhancer")
    void addStudyTraceEnhancerRel(@RequestBody IdPair traceEnhancerRel);
}
