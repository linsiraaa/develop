package com.example.one;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径 "/" 重定向到 "/login2.html"
      registry.addViewController("/").setViewName("forward:/login2.html");
  }
}
