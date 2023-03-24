package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.entity.Permission;
import pers.juumii.entity.Role;
import pers.juumii.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaAuthorizationImpl implements StpInterface {

    private final UserMapper userMapper;

    @Autowired
    public SaAuthorizationImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return userMapper.getPermissions(Convert.toLong(loginId))
                .stream().map(Permission::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userMapper.getRoles(Convert.toLong(loginId))
                .stream().map(Role::getName)
                .collect(Collectors.toList());
    }
}
