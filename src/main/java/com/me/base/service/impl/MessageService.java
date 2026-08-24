package com.me.base.service.impl;

import com.me.base.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Implementation of IMessageService.
 * Retrieves localized messages from message resource bundles.
 * 
 * @author Base Project
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    
    private final MessageSource messageSource;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
