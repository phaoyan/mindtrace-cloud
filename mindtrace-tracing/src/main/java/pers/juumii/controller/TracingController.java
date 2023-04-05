package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.MindtraceDTO;
import pers.juumii.service.TracingService;

@RestController
@RequestMapping("/trace/user/{userId}")
public class TracingController {

    private final TracingService tracingService;

    @Autowired
    public TracingController(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @PostMapping
    public Object record(
            @PathVariable Long userId,
            @RequestBody MindtraceDTO dto){
        return tracingService.record(userId, dto);
    }

    @DeleteMapping("/{traceId}")
    public Object delete(
            @PathVariable Long userId,
            @PathVariable Long traceId){
        return tracingService.delete(userId, traceId);
    }

    @PostMapping("/{traceId}")
    public Object modify(
            @PathVariable Long userId,
            @PathVariable Long traceId,
            @RequestBody MindtraceDTO dto){
        return tracingService.modify(userId, traceId, dto);
    }

    @GetMapping("/{traceId}")
    public Object check(
            @PathVariable Long userId,
            @PathVariable Long traceId){
        return tracingService.check(userId, traceId);
    }

}
