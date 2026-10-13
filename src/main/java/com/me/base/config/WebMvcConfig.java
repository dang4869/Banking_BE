package com.me.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads/kyc");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/api/kyc/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
