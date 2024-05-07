package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.temp.CurrentStudy;
import pers.juumii.dto.tracing.CurrentStudyDTO;
import pers.juumii.dto.tracing.StudyTraceDTO;
import pers.juumii.service.CurrentStudyService;

import java.util.List;

@RestController
public class CurrentStudyController {

    private final CurrentStudyService currentStudyService;

    @Autowired
    public CurrentStudyController(CurrentStudyService currentStudyService) {
        this.currentStudyService = currentStudyService;
    }

    @PostMapping("study/current")
    public CurrentStudyDTO startCurrentStudy(@RequestParam(required = false) Long traceId){
        return traceId == null ?
                CurrentStudy.transfer(currentStudyService.startCurrentStudy()) :
                CurrentStudy.transfer(currentStudyService.restartStudy(traceId));
    }

    @PostMapping("/study/current/start")
    public CurrentStudyDTO updateStartTime(@RequestParam String startTime){
        return CurrentStudy.transfer(currentStudyService.updateStartTime(startTime));
    }

    @PostMapping("/study/current/end")
    public CurrentStudyDTO updateEndTime(@RequestParam String endTime){
        return CurrentStudy.transfer(currentStudyService.updateEndTime(endTime));
    }

    @PostMapping("/study/current/duration-offset")
    public CurrentStudyDTO updateDurationOffset(@RequestParam Long offset){
        return CurrentStudy.transfer(currentStudyService.updateDurationOffset(offset));
    }

    @GetMapping("/study/current")
    public CurrentStudyDTO getCurrentStudy(){
        return CurrentStudy.transfer(currentStudyService.getCurrentStudy());
    }

    @DeleteMapping("/study/current")
    public void removeCurrentStudy(){
        currentStudyService.removeCurrentStudy();
    }

    @PostMapping("/study/current/settle")
    public StudyTraceDTO settleCurrentStudy(){
        return StudyTrace.transfer(currentStudyService.settleCurrentStudy());
    }


    @PostMapping("/study/current/pause")
    public CurrentStudyDTO pauseCurrentStudy(){
        return CurrentStudy.transfer(currentStudyService.pauseCurrentStudy());
    }

    @PostMapping("/study/current/continue")
    public CurrentStudyDTO continueCurrentStudy(){
        return CurrentStudy.transfer(currentStudyService.continueCurrentStudy());
    }

    @PostMapping("/study/current/knode/{knodeId}")
    public List<String> addTraceCoverage(@PathVariable Long knodeId){
        return currentStudyService.addKnodeId(knodeId).stream().map(Object::toString).toList();
    }

    @DeleteMapping("/study/current/knode/{knodeId}")
    public void removeTraceCoverage(@PathVariable Long knodeId){
        currentStudyService.removeKnodeId(knodeId);
    }

    @PostMapping("/study/current/enhancer/{enhancerId}")
    public List<String> addTraceEnhancerRel(@PathVariable Long enhancerId){
        return currentStudyService.addTraceEnhancerRel(enhancerId).stream().map(Object::toString).toList();
    }

    @DeleteMapping("/study/current/enhancer/{enhancerId}")
    public void removeTraceEnhancerRel(@PathVariable Long enhancerId){
        currentStudyService.removeTraceEnhancerRel(enhancerId);
    }


    @PostMapping("/study/current/title")
    public CurrentStudyDTO editCurrentStudyTitle(@RequestParam("title") String title){
        return CurrentStudy.transfer(currentStudyService.editCurrentStudyTitle(title));
    }





}
