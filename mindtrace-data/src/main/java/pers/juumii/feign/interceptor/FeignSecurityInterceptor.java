package pers.juumii.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignSecurityInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        try{
//            requestTemplate.header("cookie", SaHolder.getRequest().getHeader("cookie"));
//        }catch (Throwable ignored){}
        requestTemplate.header("admin-pass", System.getenv("MINDTRACE_SECRET"));
    }
}
