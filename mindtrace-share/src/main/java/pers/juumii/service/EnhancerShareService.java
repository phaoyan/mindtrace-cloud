package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;

public interface EnhancerShareService {
    SaResult shareEnhancer(Long userId, Long enhancerId);

    SaResult hideEnhancer(Long userId, Long enhancerId);
}
