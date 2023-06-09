package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Label;

import java.util.List;

@Service
public interface LabelService {
    List<Label> checkAll();

    SaResult create(String name);

    SaResult remove(String name);

    SaResult update(String name, Label label);
}
