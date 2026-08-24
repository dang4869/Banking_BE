package com.me.base.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.Locale;

/**
 * Configuration for Internationalization (i18n).
 * Supports multiple languages based on Accept-Language header.
 * <p>
 * Supported locales:
 * - en (English) - default
 * - vi (Vietnamese)
 * <p>
 * Usage in requests:
 * Add header: Accept-Language: vi
 * Or: Accept-Language: en
 * 
 * @author Base Project
 * @version 1.0
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {
    
    /**
     * Configures the MessageSource for reading message resource bundles.
     * Messages are loaded from src/main/resources/i18n/messages_*.properties
     * 
     * @return configured MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = 
            new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setFallbackToSystemLocale(false);
        // Enable caching for production, set to -1 to reload for development
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
    
    /**
     * Configures LocaleResolver to determine current locale from Accept-Language header.
     * 
     * @return configured LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setSupportedLocales(Arrays.asList(
            Locale.ENGLISH,
            new Locale("vi")
        ));
        return resolver;
    }
    
    /**
     * Configures interceptor to allow locale change via request parameter.
     * Optional: allows changing locale via ?lang=vi parameter.
     * 
     * @return configured LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
    
    /**
     * Registers the locale change interceptor.
     * 
     * @param registry interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
