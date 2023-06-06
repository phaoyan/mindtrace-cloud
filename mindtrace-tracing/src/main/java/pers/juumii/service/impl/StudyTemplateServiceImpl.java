package pers.juumii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.StudyTemplate;
import pers.juumii.dto.StudyTemplateDTO;
import pers.juumii.mapper.StudyTemplateMapper;
import pers.juumii.service.StudyTemplateService;

import java.util.List;

@Service
public class StudyTemplateServiceImpl implements StudyTemplateService {

    private final StudyTemplateMapper studyTemplateMapper;

    @Autowired
    public StudyTemplateServiceImpl(StudyTemplateMapper studyTemplateMapper) {
        this.studyTemplateMapper = studyTemplateMapper;
    }

    @Override
    @Transactional
    public StudyTemplate postTemplate(StudyTemplateDTO data) {
        return data.getId() != null ? updateTemplate(data) : insertTemplate(data);
    }

    private StudyTemplate insertTemplate(StudyTemplateDTO data) {
        StudyTemplate template = StudyTemplate.prototype(data);
        studyTemplateMapper.insert(template);
        return template;
    }

    private StudyTemplate updateTemplate(StudyTemplateDTO data) {
        studyTemplateMapper.updateById(StudyTemplate.prototype(data));
        return null;
    }

    @Override
    public List<StudyTemplate> getTemplates(Long userId) {
        LambdaQueryWrapper<StudyTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyTemplate::getUserId, userId);
        return studyTemplateMapper.selectList(wrapper);
    }

    @Override
    public StudyTemplate getTemplate(Long templateId) {
        return studyTemplateMapper.selectById(templateId);
    }

    @Override
    @Transactional
    public void removeTemplate(Long templateId) {
        studyTemplateMapper.deleteById(templateId);
    }
}
