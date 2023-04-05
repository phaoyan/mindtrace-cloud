package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.entity.User;

@Service
public interface UserService {
    Boolean exists(Long id);
    Boolean exists(String username);
    SaResult register(String username, String password);

    User check(Long loginId);
}
