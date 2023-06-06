package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.StudyTemplate;
import pers.juumii.dto.StudyTemplateDTO;
import pers.juumii.dto.StudyTraceDTO;
import pers.juumii.service.StudyTemplateService;

import java.util.List;

@RestController
public class StudyTemplateController {

    private final StudyTemplateService studyTemplateService;

    @Autowired
    public StudyTemplateController(StudyTemplateService studyTemplateService) {
        this.studyTemplateService = studyTemplateService;
    }

    @PostMapping("/study/template")
    public StudyTemplateDTO postTemplate(@RequestBody StudyTemplateDTO data){
        return StudyTemplate.transfer(studyTemplateService.postTemplate(data));
    }

    @GetMapping("/study/template")
    public List<StudyTemplateDTO> getTemplates(@RequestParam("userId") Long userId){
        return StudyTemplate.transfer(studyTemplateService.getTemplates(userId));
    }

    @GetMapping("/study/template/{templateId}")
    public StudyTemplateDTO getTemplate(@PathVariable Long templateId){
        return StudyTemplate.transfer(studyTemplateService.getTemplate(templateId));
    }

    @DeleteMapping("/study/template/{templateId}")
    public void removeTemplate(@PathVariable Long templateId){
        studyTemplateService.removeTemplate(templateId);
    }

}
