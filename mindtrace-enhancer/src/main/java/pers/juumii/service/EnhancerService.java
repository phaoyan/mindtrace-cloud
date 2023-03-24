package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.utils.SaResult;

@Service
public interface EnhancerService {
    SaResult query(Long userId, Long enhancerId);

    SaResult create(Long userId, EnhancerDTO dto);

    SaResult update(Long userId, Long enhancerId, EnhancerDTO dto);

    SaResult delete(Long userId, Long enhancerId);

    // 为enhancer挂载resource
    SaResult attach(Long enhancerId, Long resourceId);

    // 将enhancer与resource解绑
    SaResult detach(Long enhancerId, Long resourceId);

    SaResult label(Long enhancerId, String labelName);

    SaResult unlabel(Long enhancerId, String labelName);

}
