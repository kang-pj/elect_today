package com.example.homepage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RegionInterceptor regionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(regionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/region-select",
                    "/api/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                );
    }
}
