package pers.juumii.service.impl;

import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.CheckLabelExistence;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.mapper.LabelMapper;
import pers.juumii.service.LabelService;
import pers.juumii.utils.SaResult;

import java.util.List;

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
    public SaResult create(LabelDTO dto) {
        labelMapper.insert(Label.prototype(dto));
        return SaResult.ok();
    }

    @Override
    @CheckLabelExistence
    public SaResult delete(String name) {
        labelMapper.deleteById(name);
        return SaResult.ok();
    }

    @Override
    @CheckLabelExistence
    public SaResult update(String name, LabelDTO dto) {
        Label label = labelMapper.selectById(name);
        Opt.of(dto.getCreateBy()).ifPresent(label::setCreateBy);
        Opt.of(dto.getCreateTime()).ifPresent(label::setCreateTime);
        Opt.of(dto.getDeleted()).ifPresent(label::setDeleted);
        labelMapper.updateById(label);
        return SaResult.ok();
    }
}
