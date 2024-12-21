package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.DESCRIPTION_NOT_AVAILABLE;

@Slf4j
@Service
public class ContentService {

    @Value("${app.directories.descriptions}")
    private String descriptionsDirectory;

    @Value("${app.descriptions-extension}")
    private String descriptionsExtension;

    public String getText(String subdirectory, String key, Locale locale) {
        String language = locale.getLanguage();
        String filename = descriptionsDirectory + subdirectory + "/" + key + "_" + language + descriptionsExtension;
        Resource resource = new ClassPathResource(filename);

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load resource: {}", filename, e);
            return ErrorLocalizer.getLocalizedMessage(DESCRIPTION_NOT_AVAILABLE);
        }
    }
}