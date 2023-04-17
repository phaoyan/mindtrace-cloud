package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Enhancer;

import java.util.List;

@Service
public interface EnhancerService {


    List<Enhancer> getAllEnhancersFromUser(Long userId);

    List<Enhancer> getEnhancersFromKnode(Long knodeId);

    Enhancer getEnhancerById(Long enhancerId);

    Enhancer addEnhancerToUser(Long userId);

    Enhancer addEnhancerToKnode(Long userId, Long knodeId);

    SaResult updateEnhancerOfUser(Long userId, Long enhancerId, Enhancer updated);

    void removeEnhancerFromUser(Long userId, Long enhancerId);

    // 与用户连接
    SaResult connectToUser(Long userId, Long enhancerId);

    // 与用户解绑
    SaResult disconnectFromUser(Long userId, Long enhancerId);

    SaResult label(Long enhancerId, String labelName);

    SaResult unlabel(Long enhancerId, String labelName);

    SaResult connectEnhancerToKnode(Long userId, Long knodeId, Long enhancerId);

    SaResult disconnectEnhancerToKnode(Long userId, Long knodeId, Long enhancerId);

    Enhancer addEnhancerWithQuizcardToKnode(Long userId, Long knodeId);

}
