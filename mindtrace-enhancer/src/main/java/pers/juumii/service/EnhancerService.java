package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.dto.EnhancerDTO;

@Service
public interface EnhancerService {


    SaResult queryByUserId(Long userId, Long enhancerId);

    SaResult queryByKnodeId(Long knodeId, Long enhancerId);

    SaResult create(Long userId, EnhancerDTO dto);

    SaResult update(Long userId, Long enhancerId, EnhancerDTO dto);

    SaResult delete(Long userId, Long enhancerId);

    // 与用户连接
    SaResult connect(Long userId, Long enhancerId);

    // 与用户解绑
    SaResult disconnect(Long userId, Long enhancerId);

    // 与Knode连接
    SaResult use(Long knodeId, Long enhancerId);

    // 与Knode解绑
    SaResult drop(Long knodeId, Long enhancerId);

    // 为enhancer挂载resource
    SaResult attach(Long enhancerId, Long resourceId);

    // 将enhancer与resource解绑
    SaResult detach(Long enhancerId, Long resourceId);

    SaResult label(Long enhancerId, String labelName);

    SaResult unlabel(Long enhancerId, String labelName);

}
