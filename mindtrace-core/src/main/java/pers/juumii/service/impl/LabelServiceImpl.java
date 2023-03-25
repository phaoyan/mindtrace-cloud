package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.repo.LabelRepository;
import pers.juumii.service.LabelService;

import java.util.List;
import java.util.Optional;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepo;

    @Autowired
    public LabelServiceImpl(LabelRepository labelRepo) {
        this.labelRepo = labelRepo;
    }

    @Override
    public List<Label> checkAll() {
        return labelRepo.findAll();
    }

    @Override
    public SaResult create(String name) {
        Label label = labelRepo.save(Label.prototype(name));
        return SaResult.data(label);
    }

    @Override
    public SaResult remove(String name) {
        Optional<Label> optional = labelRepo.findById(name);
        optional.ifPresent(label -> labelRepo.deleteById(label.getName()));
        return SaResult.ok();
    }

    @Override
    public SaResult update(String name, LabelDTO dto) {
        Optional<Label> optional = labelRepo.findById(name);
        if(optional.isEmpty())
            return SaResult.error("Label not found: " + name);
        Label Label = optional.get();
        Opt.ifPresent(dto.getName(), Label::setName);
        Opt.ifPresent(dto.getDeleted(), Label::setDeleted);
        labelRepo.save(Label);
        return SaResult.data(Label);
    }
}
