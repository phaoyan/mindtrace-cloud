package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.utils.SaResult;

@Service
public interface UserService {

    SaResult register(Long id, String name);

    // 返回user所有root的id
    SaResult check(Long id);

    SaResult remove(Long id);

    // 为user创建一个根knode（学科）
    SaResult branch(Long id, String title);

    SaResult dropBranch(Long userId, Long branchId);

    SaResult attachBranch(Long userId, Long branchId);

}
