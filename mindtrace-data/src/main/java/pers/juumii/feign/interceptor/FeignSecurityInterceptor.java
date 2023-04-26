package pers.juumii.feign.interceptor;

import cn.dev33.satoken.context.SaHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignSecurityInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("cookie", SaHolder.getRequest().getHeader("cookie"));
    }
}
