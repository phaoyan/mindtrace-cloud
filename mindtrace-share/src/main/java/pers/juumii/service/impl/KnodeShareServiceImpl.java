package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.service.KnodeShareService;

@Service
public class KnodeShareServiceImpl implements KnodeShareService {
    @Override
    public SaResult shareKnode(Long userId, Long knodeId) {
        return null;
    }

    @Override
    public SaResult hideKnode(Long userId, Long knodeId) {
        return null;
    }
}
