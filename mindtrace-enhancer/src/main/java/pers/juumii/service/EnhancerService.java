package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;

import java.util.List;

@Service
public interface EnhancerService {


    List<Enhancer> getAllEnhancersFromUser(Long userId);

    List<Enhancer> queryByKnodeId(Long knodeId);

    Enhancer getEnhancerFromUser(Long userId, Long enhancerId);

    Enhancer addEnhancerToUser(Long userId);

    SaResult updateEnhancerOfUser(Long userId, Long enhancerId, Enhancer updated);

    SaResult removeEnhancerFromUser(Long userId, Long enhancerId);

    // 与用户连接
    SaResult connect(Long userId, Long enhancerId);

    // 与用户解绑
    SaResult disconnect(Long userId, Long enhancerId);

    // 为enhancer挂载resource
    SaResult attach(Long enhancerId, Long resourceId);

    // 将enhancer与resource解绑
    SaResult detach(Long enhancerId, Long resourceId);

    SaResult label(Long enhancerId, String labelName);

    SaResult unlabel(Long enhancerId, String labelName);

    SaResult connectEnhancerToKnode(Long userId, Long enhancerId, Long knodeId);

    SaResult disconnectEnhancerToKnode(Long userId, Long enhancerId, Long knodeId);
}
