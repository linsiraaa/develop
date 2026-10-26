package com.example.one;

import com.example.one.security.JwtAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   @Autowired
   private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径 "/" 重定向到 "/login2.html"
       registry.addViewController("/").setViewName("forward:/login2.html");
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(jwtAuthenticationInterceptor)
               .addPathPatterns("/api/**");
   }
}
