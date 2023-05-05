package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.UserShare;
import pers.juumii.mapper.UserShareMapper;
import pers.juumii.service.UserShareService;
import pers.juumii.utils.AuthUtils;

@Service
public class UserShareServiceImpl implements UserShareService {

    private final UserShareMapper userShareMapper;
    private final AuthUtils authUtils;

    @Autowired
    public UserShareServiceImpl(
            UserShareMapper userShareMapper,
            AuthUtils authUtils) {
        this.userShareMapper = userShareMapper;
        this.authUtils = authUtils;
    }

    @Override
    public void openUserShare(Long userId) {
        authUtils.same(userId);
        userShareMapper.insert(UserShare.prototype(userId));
    }

    @Override
    public void closeUserShare(Long userId) {
        authUtils.same(userId);
        userShareMapper.deleteByUserId(userId);
    }

    @Override
    public UserShare getUserShare(Long userId) {
        authUtils.auth(userId);
        return userShareMapper.selectByUserId(userId);
    }
}
