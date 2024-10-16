package com.javarush.quest.shubchynskyi.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentServiceTest {

    private final ContentService contentService = new ContentService();

    private String readExpected(String subdirectory, String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("expected/" + subdirectory + "/" + filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    public void testGetTextWithExistingLocaleAndKey() throws IOException {
        String subdirectory = "index_page";
        String key = "description";
        Locale locale = Locale.forLanguageTag("en");

        String expected = readExpected(subdirectory, "description_en.txt");
        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTextWithNonExistingLocale() {
        String subdirectory = "index_page";
        String key = "description";
        Locale locale = Locale.forLanguageTag("zz"); // Unlikely locale code

        String expected = "Description not available";
        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTextWithNonExistingKey() {
        String subdirectory = "index_page";
        String key = "nonexistent_key";
        Locale locale = Locale.forLanguageTag("en");

        String expected = "Description not available";
        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(expected, actual);
    }
}