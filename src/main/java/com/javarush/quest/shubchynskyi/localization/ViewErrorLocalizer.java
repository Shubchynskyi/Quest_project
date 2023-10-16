package com.javarush.quest.shubchynskyi.localization;

import lombok.experimental.UtilityClass;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.ResourceBundle;

@UtilityClass
public class ViewErrorLocalizer {

    public static String getLocalizedMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
