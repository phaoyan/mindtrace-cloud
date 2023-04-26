package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.dto.ResourceInfoDTO;
import pers.juumii.service.ResourceShareService;

@Service
public class ResourceShareServiceImpl implements ResourceShareService {
    @Override
    public SaResult updateResourceInfo(Long userId, Long resourceId, ResourceInfoDTO dto) {
        return null;
    }

    @Override
    public SaResult shareResource(Long userId, Long resourceId) {
        return null;
    }

    @Override
    public SaResult hideResource(Long userId, Long resourceId) {
        return null;
    }
}
