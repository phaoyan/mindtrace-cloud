package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import pers.juumii.dto.ResourceInfoDTO;

public interface ResourceShareService {
    SaResult updateResourceInfo(Long userId, Long resourceId, ResourceInfoDTO dto);

    SaResult shareResource(Long userId, Long resourceId);

    SaResult hideResource(Long userId, Long resourceId);

}
