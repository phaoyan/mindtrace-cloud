package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import pers.juumii.entity.User;

public interface LoginService {
    SaResult login(User user);
}
