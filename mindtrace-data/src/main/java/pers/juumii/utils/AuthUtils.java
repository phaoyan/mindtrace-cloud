package pers.juumii.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.juumii.feign.ShareClient;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthUtils {

    public static final String ADMIN_PASS = System.getenv("MINDTRACE_SECRET");

    //用于严格鉴权
    public void same(Long userId){
        if(userId == null) return;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String adminPass = request.getHeader("admin-pass");
        if(adminPass != null && adminPass.equals(ADMIN_PASS)) return;
        Long loginId = StpUtil.getLoginIdAsLong();
        if(!loginId.equals(userId))
            throw new RuntimeException("Authentication failed: not allowed to visit user " + userId);
    }

    public void admin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String adminPass = request.getHeader("admin-pass");
        if(adminPass != null &&  adminPass.equals(ADMIN_PASS)) return;
        throw new RuntimeException("Authentication failed: This API is only for admin");
    }
}
