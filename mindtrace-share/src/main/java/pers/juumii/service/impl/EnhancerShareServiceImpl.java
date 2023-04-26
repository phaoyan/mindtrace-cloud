package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.service.EnhancerShareService;

@Service
public class EnhancerShareServiceImpl implements EnhancerShareService {
    @Override
    public SaResult shareEnhancer(Long userId, Long enhancerId) {
        return null;
    }

    @Override
    public SaResult hideEnhancer(Long userId, Long enhancerId) {
        return null;
    }
}
