package org.example.clouddemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @author hzg
 * @version 2024/02/21 15:35:18
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private TokenInterceptor tokenInterceptor;

    public static List<String> whitelists = Arrays.asList(
            "/doc.html", "/webjars/**", "/img.icons/**", "/swagger-resources/**", "/v2/api-docs", "/swagger-ui.html"
            , "/druid/**", "/file/**"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**") // 拦截所有请求
                .excludePathPatterns(whitelists) // 放行: 接口文档相关的路径
        ;

    }

}
