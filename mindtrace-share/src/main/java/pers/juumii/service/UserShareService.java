package pers.juumii.service;

import pers.juumii.data.UserShare;

public interface UserShareService {
    void openUserShare(Long userId);
    void closeUserShare(Long userId);
    UserShare getUserShare(Long userId);
}
