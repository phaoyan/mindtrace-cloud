package pers.juumii.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;
import pers.juumii.feign.ShareClient;

@Component
public class AuthUtils {

    public static final String ADMIN_PASS = "ADMIN_PASS";

    private final ShareClient shareClient;

    public AuthUtils(ShareClient shareClient) {
        this.shareClient = shareClient;
    }

    //用于鉴权
    public void auth(Long userId){
        if(userId == null) return;
        Long loginId = StpUtil.getLoginIdAsLong();
        if(!loginId.equals(userId) && !shareClient.isUserPublic(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }

    public void auth(Long userId, Long loginId){
        if(userId == null) return;
        if(loginId == null)
            auth(userId);
        else if(!loginId.equals(userId) && !shareClient.isUserPublic(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }

    //用于严格鉴权
    public void same(Long userId){
        Long loginId = StpUtil.getLoginIdAsLong();
        if(!loginId.equals(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }
}
