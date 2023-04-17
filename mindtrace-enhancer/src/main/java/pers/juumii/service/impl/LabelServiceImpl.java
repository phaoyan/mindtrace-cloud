package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Label;
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
    public List<Label> getAll() {
        return labelMapper.selectList(null);
    }

    @Override
    public SaResult create(String labelName, Label label) {
        if(!Objects.isNull(labelMapper.selectById(labelName)))
            return SaResult.error("Label already exists: " + labelName);
        labelMapper.insert(label);
        return SaResult.ok();
    }

    @Override
    public SaResult delete(String labelName) {
        labelMapper.deleteById(labelName);
        return SaResult.ok();
    }

    @Override
    public SaResult update(String labelName, Label newLabel) {
        Label label = labelMapper.selectById(labelName);
        Opt.ofNullable(newLabel.getCreateBy()).ifPresent(label::setCreateBy);
        Opt.ofNullable(newLabel.getCreateTime()).ifPresent(label::setCreateTime);
        Opt.ofNullable(newLabel.getDeleted()).ifPresent(label::setDeleted);
        labelMapper.updateById(label);
        return SaResult.ok();
    }
}
