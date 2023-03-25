package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.CheckLabelExistence;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.service.LabelService;

import java.util.List;
import java.util.Objects;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelMapper labelMapper;

    @Autowired
    public LabelServiceImpl(LabelMapper labelMapper) {
        this.labelMapper = labelMapper;
    }

    @Override
    public SaResult queryAll() {
        List<Label> all = labelMapper.selectList(null);
        return SaResult.data(all);
    }

    @Override
    @CheckLabelExistence
    public SaResult create(String labelName, LabelDTO dto) {
        if(!Objects.isNull(labelMapper.selectById(labelName)))
            return SaResult.error("Label already exists: " + labelName);
        labelMapper.insert(Label.prototype(dto));
        return SaResult.ok();
    }

    @Override
    public SaResult delete(String labelName) {
        labelMapper.deleteById(labelName);
        return SaResult.ok();
    }

    @Override
    @CheckLabelExistence
    public SaResult update(String labelName, LabelDTO dto) {
        Label label = labelMapper.selectById(labelName);
        Opt.ofNullable(dto.getCreateBy()).ifPresent(label::setCreateBy);
        Opt.ofNullable(dto.getCreateTime()).ifPresent(label::setCreateTime);
        Opt.ofNullable(dto.getDeleted()).ifPresent(label::setDeleted);
        labelMapper.updateById(label);
        return SaResult.ok();
    }
}
