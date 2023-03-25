package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.dto.LabelDTO;

@Service
public interface LabelService {
    SaResult queryAll();

    SaResult create(String name, LabelDTO dto);

    SaResult delete(String name);

    SaResult update(String name, LabelDTO dto);
}
