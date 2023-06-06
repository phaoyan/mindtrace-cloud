package pers.juumii.service;


import pers.juumii.data.StudyTemplate;
import pers.juumii.dto.StudyTemplateDTO;

import java.util.List;

public interface StudyTemplateService {
    StudyTemplate postTemplate(StudyTemplateDTO data);

    List<StudyTemplate> getTemplates(Long userId);

    StudyTemplate getTemplate(Long templateId);

    void removeTemplate(Long templateId);
}
