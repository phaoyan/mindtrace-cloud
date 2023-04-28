package pers.juumii.service.impl.v1;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Label;
import pers.juumii.repo.LabelRepository;
import pers.juumii.service.LabelService;

import java.util.List;
import java.util.Optional;

//@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepo;

    @Autowired
    public LabelServiceImpl(LabelRepository labelRepo) {
        this.labelRepo = labelRepo;
    }

    @Override
    public List<Label> checkAll() {
        return null;
    }

    @Override
    public SaResult create(String name) {
        return null;
    }

    @Override
    public SaResult remove(String name) {
        return null;
    }

    @Override
    public SaResult update(String name, Label newLabel) {
        return null;

    }
}
