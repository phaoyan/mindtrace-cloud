package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    Boolean exists(Long id);
}
