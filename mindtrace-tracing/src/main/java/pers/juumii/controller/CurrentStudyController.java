package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.temp.CurrentStudy;
import pers.juumii.dto.CurrentStudyDTO;
import pers.juumii.dto.StudyTraceDTO;
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
    public CurrentStudyDTO startCurrentStudy(@RequestParam(value = "userId", required = false) Long userId){
        return CurrentStudy.transfer(currentStudyService.startCurrentStudy(userId));
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
