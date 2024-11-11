package com.javarush.quest.shubchynskyi.test_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@TestConfiguration
public class TestLocaleConfiguration {

    @Value("${app.localization.default-locale}")
    private String defaultLocale;

    @EventListener(ApplicationReadyEvent.class)
    public void setDefaultLocale() {
        Locale locale = Locale.forLanguageTag(defaultLocale);
        Locale.setDefault(locale);
        LocaleContextHolder.setLocale(locale);
    }
}
