package pers.juumii.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * [Sa-Token 权限认证] 全局配置类
 */
@Configuration
public class SaTokenConfigure implements WebFluxConfigurer {
    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
//                // 指定 [拦截路由]
//                .addInclude("/**")    /* 拦截所有path */
//                // 指定 [放行路由]
//                .addExclude("/favicon.ico")
//                // 指定[认证函数]: 每次请求执行
//                .setAuth(obj ->{
//                    SaRouter.match("/**")
//                        .notMatch("/user/login")
//                        .notMatch("/user/register/**")
//                        .notMatch("/hub/resource/**")
//                        .check(()->{
//                            String adminPass = SaHolder.getRequest().getHeader("admin-pass");
//                            if(adminPass == null || !adminPass.equals(System.getenv("MINDTRACE_SECRET")))
//                                StpUtil.checkLogin();
//                        });
//                    SaRouter.match("/hub/resource/**")
//                            .notMatchMethod(String.valueOf(SaHttpMethod.GET))
//                            .check(()->{
//                                String adminPass = SaHolder.getRequest().getHeader("admin-pass");
//                                if(adminPass == null || !adminPass.equals(System.getenv("MINDTRACE_SECRET")))
//                                    StpUtil.checkLogin();
//                            });
//                    })
                // 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数
                .setError(e -> {
                    SaRequest request = SaHolder.getRequest();
                    // 处理预检请求
                    if(SaHolder.getRequest().getMethod().equals("OPTIONS")){
                        try {
                            URL url = new URL(request.getUrl());
                            SaHolder.getResponse()
                                    .addHeader("Access-Control-Allow-Credentials","true")
                                    .addHeader("Access-Control-Allow-Origin",
                                            url.getHost().equals("localhost") ?
                                            "http://"+System.getenv("LOCAL_HOST") :
                                            "http://"+System.getenv("SERVER_HOST"))
                                    .addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                                    .addHeader("Access-Control-Allow-Headers","Content-Type,Authorization,x-requested-with");

                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        }

                        return SaResult.ok();
                    }

                    System.out.println(" sa global error : " + request.getUrl());
                    e.printStackTrace();
                    return SaResult.error(e.getMessage());
                });
    }
}
