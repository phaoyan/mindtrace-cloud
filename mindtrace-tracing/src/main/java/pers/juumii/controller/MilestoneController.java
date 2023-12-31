package pers.juumii.controller;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.Milestone;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.tracing.MilestoneDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.service.MilestoneService;
import pers.juumii.service.StudyTraceService;
import pers.juumii.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class MilestoneController {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final AuthUtils authUtils;
    private final MilestoneService milestoneService;
    private final StudyTraceService studyTraceService;

    @Autowired
    public MilestoneController(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            AuthUtils authUtils,
            MilestoneService milestoneService,
            StudyTraceService studyTraceService) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.authUtils = authUtils;
        this.milestoneService = milestoneService;
        this.studyTraceService = studyTraceService;
    }

    private Long knodeSameUser(Long knodeId){
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
        return Convert.toLong(knode.getCreateBy());
    }

    private void milestoneSameUser(Long id){
        Milestone milestone = milestoneService.getById(id);
        authUtils.same(milestone.getUserId());
    }

    private void resourceSameUser(Long resourceId){
        ResourceDTO resource = enhancerClient.getResourceById(resourceId);
        authUtils.same(Convert.toLong(resource.getCreateBy()));
    }

    private void traceSameUser(Long traceId){
        StudyTrace studyTrace = studyTraceService.getStudyTrace(traceId);
        authUtils.same(studyTrace.getUserId());
    }

    @PutMapping("/milestone")
    public MilestoneDTO add(@RequestParam Long knodeId){
        Long userId = knodeSameUser(knodeId);
        return Milestone.transfer(milestoneService.add(knodeId, userId));
    }

    @DeleteMapping("/milestone/{id}")
    public void remove(@PathVariable Long id){
        milestoneSameUser(id);
        milestoneService.remove(id);
    }

    @PostMapping("/milestone/{id}/knode")
    public void setKnodeId(@PathVariable Long id, @RequestParam Long knodeId){
        milestoneSameUser(id);
        knodeSameUser(knodeId);
        milestoneService.setKnodeId(id, knodeId);
    }

    @PostMapping("/milestone/{id}/description")
    public  void setDescription(@PathVariable Long id, @RequestBody String description){
        milestoneSameUser(id);
        milestoneService.setDescription(id, description);
    }

    @PostMapping("/milestone/{id}/time")
    public void setTime(@PathVariable Long id, @RequestParam String dateTime){
        milestoneSameUser(id);
        milestoneService.setTime(id, dateTime);
    }

    @PutMapping("/milestone/{id}/resource")
    public ResourceDTO addResource(@PathVariable Long id, @RequestParam String type){
        milestoneSameUser(id);
        return milestoneService.addResource(id, type);
    }

    @DeleteMapping("/milestone/resource/{resourceId}")
    public void removeResource(@PathVariable Long resourceId){
        resourceSameUser(resourceId);
        milestoneService.removeResource(resourceId);
    }

    @GetMapping("/milestone/{id}")
    public MilestoneDTO getById(@PathVariable Long id){
        return Milestone.transfer(milestoneService.getById(id));
    }

    @GetMapping("/milestone")
    public List<MilestoneDTO> getMilestonesBeneathKnode(@RequestParam Long knodeId){
        return Milestone.transfer(milestoneService.getMilestonesBeneathKnode(knodeId));
    }

    @GetMapping("/milestone/{id}/resource")
    public List<ResourceDTO> getResourcesFromMilestone(@PathVariable Long id){
        return milestoneService.getResourcesFromMilestone(id);
    }

    @GetMapping("/rel/milestone/trace")
    public List<?> getMilestoneTraceRels(
            @RequestParam(required = false) Long milestoneId,
            @RequestParam(required = false) Long traceId){
        if(milestoneId != null)
            return milestoneService.getStudyTraces(milestoneId).stream()
                .map(rel->studyTraceService.getStudyTrace(rel.getTraceId()))
                .filter(Objects::nonNull)
                .map(StudyTrace::transfer)
                .toList();
        else if(traceId != null)
            return milestoneService.getMilestones(traceId).stream()
                .map(rel->milestoneService.getById(rel.getMilestoneId()))
                .filter(Objects::nonNull)
                .map(Milestone::transfer)
                .toList();
        else return new ArrayList<>();
    }

    @PostMapping("/batch/exist/rel/milestone/trace")
    public List<String> getTracesInMilestones(@RequestBody List<Long> traceIds){
        return milestoneService.getTracesInMilestones(traceIds).stream().map(Convert::toStr).toList();
    }


    @PutMapping("/rel/milestone/trace")
    public void addMilestoneTraceRel(@RequestParam Long milestoneId, @RequestParam Long traceId){
        milestoneSameUser(milestoneId);
        traceSameUser(traceId);
        milestoneService.addStudyTrace(milestoneId, traceId);
    }

    @DeleteMapping("/rel/milestone/trace")
    public void removeMilestoneTraceRel(@RequestParam Long milestoneId, @RequestParam Long traceId){
        milestoneSameUser(milestoneId);
        traceSameUser(traceId);
        milestoneService.removeStudyTrace(milestoneId, traceId);
    }


    @PostMapping("/milestone/copy")
    public void copyMilestoneAsEnhancerToKnode(@RequestParam Long milestoneId, @RequestParam Long knodeId){
        milestoneSameUser(milestoneId);
        knodeSameUser(knodeId);
        milestoneService.copyMilestoneAsEnhancerToKnode(milestoneId, knodeId);
    }
}
