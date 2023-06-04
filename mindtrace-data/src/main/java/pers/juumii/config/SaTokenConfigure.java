package pers.juumii.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类
 * 作用：外部无法直接通过ip访问服务，必须通过gateway
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    // 注册 Sa-Token 全局过滤器
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
//                .setAuth(obj -> SaSameUtil.checkCurrentRequestToken())
                .setError(e -> SaResult.error(e.getMessage()));
    }


}
