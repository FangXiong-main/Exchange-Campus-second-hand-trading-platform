package com.exchange.config;

import com.exchange.interceptor.TokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${exchange_file.local-path}")
    private String localPath;

    @Value("${exchange_file.prefix}")
    private String prefix;

    @Resource
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/adminLogin",
                        "/loginWithCode",
                        "/loginWithEmail",
                        "/sendEmailCode",
                        prefix+"**"
                );
    }

    // 配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(prefix + "**")
                .addResourceLocations("file:" + localPath);
    }
}
