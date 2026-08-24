package com.me.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 * Allows frontend applications to make requests to this API.
 * <p>
 * Current configuration allows all origins for development.
 * In production, restrict to specific frontend domains.
 * 
 * @author Base Project
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * Configures CORS mappings.
     * 
     * @param registry CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // Allow all origins (change in production)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
