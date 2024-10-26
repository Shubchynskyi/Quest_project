package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.DESCRIPTION_NOT_AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContentServiceIT {

    @Autowired
    private ContentService contentService;

    @Value("${app.localization.supported-languages}")
    private String[] supportedLanguages;

    @Value("${app.default-locale}")
    private String defaultLocaleString;

    @Value("${app.descriptions-extension}")
    private String descriptionsExtension;

    private Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }

    String subdirectory = "index_page";

    private String readExpected(String subdirectory, String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("expected/" + subdirectory + "/" + filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @ParameterizedTest
    @MethodSource("supportedLanguagesProvider")
    public void testGetTextWithExistingLocaleAndKey(String localeTag) throws IOException {
        String key = "description";
        Locale locale = Locale.forLanguageTag(localeTag);
        String filename = "description_" + localeTag + descriptionsExtension;

        String expected = readExpected(subdirectory, filename);
        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTextWithNonExistingLocale() {
        String key = "description";
        Locale locale = Locale.forLanguageTag("zz");

        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(ErrorLocalizer.getLocalizedMessage(DESCRIPTION_NOT_AVAILABLE), actual);
    }

    @Test
    public void testGetTextWithNonExistingKey() {
        String key = "nonexistent_key";
        Locale locale = Locale.forLanguageTag(defaultLocaleString);

        String actual = contentService.getText(subdirectory, key, locale);

        assertEquals(ErrorLocalizer.getLocalizedMessage(DESCRIPTION_NOT_AVAILABLE), actual);
    }
}