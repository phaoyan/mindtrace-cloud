package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.utils.SaResult;

import java.util.List;

@Service
public interface LabelService {
    List<Label> checkAll();

    SaResult create(String name);

    SaResult remove(String name);

    SaResult update(String name, LabelDTO dto);
}
