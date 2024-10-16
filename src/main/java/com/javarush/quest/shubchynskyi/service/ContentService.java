package com.javarush.quest.shubchynskyi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.io.InputStream;

@Slf4j
@Service
public class ContentService {

    // TODO refactoring
    private static final String DEFAULT_MESSAGE = "Description not available";

    public String getText(String subdirectory, String key, Locale locale) {
        String language = locale.getLanguage();
        String filename = "descriptions/" + subdirectory + "/" + key + "_" + language + ".txt";
        Resource resource = new ClassPathResource(filename);

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load resource: {}", filename, e);
            return DEFAULT_MESSAGE;
        }
    }
}