package com.me.base.service;

import java.util.Locale;

/**
 * Service interface for internationalization (i18n) message retrieval.
 * Provides methods to get localized messages based on current locale.
 * 
 * @author Base Project
 * @version 1.0
 */
public interface IMessageService {
    
    /**
     * Gets a message for the given key using current locale.
     * 
     * @param key message key from messages.properties
     * @return localized message
     */
    String getMessage(String key);
    
    /**
     * Gets a message for the given key with parameters using current locale.
     * 
     * @param key message key from messages.properties
     * @param args parameters to replace placeholders in message
     * @return localized message with replaced parameters
     */
    String getMessage(String key, Object... args);
    
    /**
     * Gets a message for the given key using specific locale.
     * 
     * @param key message key
     * @param locale specific locale
     * @return localized message
     */
    String getMessage(String key, Locale locale);
    
    /**
     * Gets a message for the given key with parameters using specific locale.
     * 
     * @param key message key
     * @param locale specific locale
     * @param args parameters to replace placeholders
     * @return localized message with replaced parameters
     */
    String getMessage(String key, Locale locale, Object... args);
}
