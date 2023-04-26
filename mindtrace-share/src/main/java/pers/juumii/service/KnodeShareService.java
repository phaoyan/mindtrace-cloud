package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;

public interface KnodeShareService {
    SaResult shareKnode(Long userId, Long knodeId);

    SaResult hideKnode(Long userId, Long knodeId);
}
