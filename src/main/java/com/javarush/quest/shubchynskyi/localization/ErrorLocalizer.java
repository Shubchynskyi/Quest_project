package com.javarush.quest.shubchynskyi.localization;

import lombok.experimental.UtilityClass;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@UtilityClass
public class ErrorLocalizer {

    public static String getLocalizedMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }

    public static ExceptionLocalizedMessage getExceptionLocalizedMessage(String messageKey) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String message;
        boolean isMessageService = false;

        try {
            message = bundle.getString(messageKey);
        } catch (MissingResourceException e) {
            message = messageKey;
            isMessageService = true;
        }

        return new ExceptionLocalizedMessage(message, isMessageService);
    }

}
