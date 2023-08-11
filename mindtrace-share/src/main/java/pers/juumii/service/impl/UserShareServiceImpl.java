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

    @Autowired
    public UserShareServiceImpl(UserShareMapper userShareMapper) {
        this.userShareMapper = userShareMapper;
    }

    @Override
    public UserShare getUserShare(Long userId) {
        UserShare userShare = userShareMapper.selectByUserId(userId);
        if(userShare == null){
            userShare = UserShare.prototype(userId);
            userShareMapper.insert(userShare);
        }
        return userShare;
    }
}
