package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.dto.ResourceDTO;

@Service
public interface ResourceService {
    SaResult fetch(Long resourceId, Long userId);

    SaResult put(Long userId, MultipartFile file, ResourceDTO meta);

    SaResult modify(Long resourceId, Long userId, ResourceDTO meta);

    SaResult alterSource(Long resourceId, Long userId, MultipartFile source);

    // 将resource与enhancer解绑（于是user同时也拿不到resource的链接）
    SaResult disconnect(Long resourceId, Long enhancerId);

}
