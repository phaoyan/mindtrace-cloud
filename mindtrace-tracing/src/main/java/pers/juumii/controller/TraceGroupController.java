package pers.juumii.controller;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceGroup;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.dto.tracing.TraceGroupDTO;
import pers.juumii.service.TraceGroupService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TraceGroupController {

    private final TraceGroupService traceGroupService;

    @Autowired
    public TraceGroupController(TraceGroupService traceGroupService) {
        this.traceGroupService = traceGroupService;
    }



    @PutMapping("/trace-group")
    public TraceGroupDTO union(
            @RequestParam List<Long> traceIds,
            @RequestParam(required = false) Long groupId){
        return TraceGroup.transfer(traceGroupService.union(traceIds, groupId));
    }

    @DeleteMapping("/trace/{traceId}/trace-group/{groupId}")
    public void remove(@PathVariable Long traceId, @PathVariable Long groupId){
        traceGroupService.remove(traceId, groupId);
    }

    @DeleteMapping("/trace/{traceId}/trace-group")
    public void remove(@PathVariable Long traceId){
        traceGroupService.remove(traceId);
    }

    @DeleteMapping("/trace-group/{groupId}")
    public void removeTraceGroup(@PathVariable Long groupId){
        traceGroupService.removeTraceGroup(groupId);
    }

    @PostMapping("/trace/trace-group")
    public List<TraceGroupDTO> getGroupsByTraceIds(@RequestBody List<Long> traceIds){
        return TraceGroup.transfer(traceGroupService.getGroupsByTraceIds(traceIds));
    }

    @PostMapping("/trace-group/mapping")
    public Map<String, String> getGroupMappingByTraceIds(@RequestBody List<Long> traceIds){
        return traceGroupService.getGroupMappingByTraceIds(traceIds)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry->Convert.toStr(entry.getKey()),
                        entry->Convert.toStr(entry.getValue())
                ));
    }

    @GetMapping("/trace-group/{groupId}/trace")
    public List<StudyTraceDTO> getTracesByGroupId(@PathVariable Long groupId){
        return StudyTrace.transfer(traceGroupService.getTracesByGroupId(groupId));
    }

    @GetMapping("/trace-group/{groupId}")
    public TraceGroupDTO getGroupById(@PathVariable Long groupId){
        return TraceGroup.transfer(traceGroupService.getGroupById(groupId));
    }

    @PostMapping("/trace-group")
    public List<TraceGroupDTO> getGroupByIdBatch(@RequestBody List<Long> groupIds){
        return new HashSet<>(groupIds).stream()
                .map(traceGroupService::getGroupById)
                .filter(Objects::nonNull)
                .map(TraceGroup::transfer)
                .toList();
    }

    @PutMapping("/trace-group/{groupId}/title")
    public void setGroupTitle(@PathVariable Long groupId, @RequestParam String title){
        traceGroupService.setGroupTitle(groupId, title);
    }


}
