package com.example.one.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 当前前端使用同源请求；保留通配来源以兼容本地开发页面。
        corsConfiguration.addAllowedOriginPattern("*");
        // 允许任何方法
        corsConfiguration.addAllowedMethod("*");
        // 允许任何头
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
