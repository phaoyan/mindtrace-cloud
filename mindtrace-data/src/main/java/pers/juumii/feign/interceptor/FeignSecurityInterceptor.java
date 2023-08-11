package pers.juumii.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class FeignSecurityInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        try{
//            requestTemplate.header("cookie", SaHolder.getRequest().getHeader("cookie"));
//        }catch (Throwable ignored){}
        try{
            requestTemplate.header("admin-pass", System.getenv("MINDTRACE_SECRET"));
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            requestTemplate.header("Cookie", request.getHeader("Cookie"));
        }catch (IllegalStateException e){}
    }
}
