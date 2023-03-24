package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Label;
import pers.juumii.dto.LabelDTO;
import pers.juumii.utils.SaResult;

@Service
public interface LabelService {
    SaResult queryAll();

    SaResult create(LabelDTO dto);

    SaResult delete(String name);

    SaResult update(String name, LabelDTO dto);
}
