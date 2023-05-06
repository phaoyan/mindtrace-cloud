package pers.juumii.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.juumii.feign.ShareClient;

import javax.servlet.http.HttpServletRequest;

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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String adminPass = request.getHeader("admin-pass");
        if(adminPass != null && adminPass.equals(ADMIN_PASS)) return;
        Long loginId = StpUtil.getLoginIdAsLong();
        if(!loginId.equals(userId) && !shareClient.isUserPublic(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }

    //用于严格鉴权
    public void same(Long userId){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String adminPass = request.getHeader("admin-pass");
        if(adminPass != null && adminPass.equals(ADMIN_PASS)) return;
        Long loginId = StpUtil.getLoginIdAsLong();
        if(!loginId.equals(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }
}
