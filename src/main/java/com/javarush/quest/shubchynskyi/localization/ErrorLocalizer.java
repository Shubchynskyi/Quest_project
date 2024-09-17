package com.javarush.quest.shubchynskyi.localization;

import com.javarush.quest.shubchynskyi.constant.Key;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
@UtilityClass
public class ErrorLocalizer {

    public static String getLocalizedMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(Key.LOCALIZATION_MESSAGES_BUNDLE, locale);
        return bundle.getString(key);
    }

    public static ExceptionLocalizedMessage getExceptionLocalizedMessage(String messageKey) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(Key.LOCALIZATION_MESSAGES_BUNDLE, locale);
        String message;
        boolean isMessageService = false;

        try {
            message = bundle.getString(messageKey);
        } catch (MissingResourceException e) {
            log.warn("Missing resource for key: {}", messageKey, e);
            message = messageKey;
            isMessageService = true;
        }

        return new ExceptionLocalizedMessage(message, isMessageService);
    }

}
