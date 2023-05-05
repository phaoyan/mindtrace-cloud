package pers.juumii.feign.interceptor;

import cn.dev33.satoken.context.SaHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import pers.juumii.utils.AuthUtils;

public class FeignSecurityInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try{
            requestTemplate.header("cookie", SaHolder.getRequest().getHeader("cookie"));
        }catch (Throwable ignored){}
        requestTemplate.header("admin-pass", AuthUtils.ADMIN_PASS);
    }
}
