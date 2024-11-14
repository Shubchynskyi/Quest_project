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

    @Value("${content.index-page.content-file-name}")
    private String indexPageDescriptionFileName;
    @Value("${content.index-page.test-directory}")
    private String indexPageTestContentDirectory;
    @Value("${content.expected-data-directory}")
    private String expectedTestContentDirectory;
    @Value("${content.invalid-directory}")
    private String invalidDirectory;

    @Value("${app.localization.supported-languages}")
    private String[] supportedLanguages;
    @Value("${app.default-locale}")
    private String defaultLocale;
    @Value("${app.non-existent-locale}")
    private String nonExistentLocale;
    @Value("${app.descriptions-extension}")
    private String descriptionsExtension;

    @Autowired
    private ContentService contentService;

    private Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }

    private String readExpected(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(expectedTestContentDirectory + indexPageTestContentDirectory + filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @ParameterizedTest
    @MethodSource("supportedLanguagesProvider")
    public void testGetTextWithExistingLocaleAndKey(String localeTag) throws IOException {
        Locale locale = Locale.forLanguageTag(localeTag);
        String filename = indexPageDescriptionFileName + "_" + localeTag + descriptionsExtension;

        String expected = readExpected(filename);
        String actual = contentService.getText(indexPageTestContentDirectory, indexPageDescriptionFileName, locale);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTextWithNonExistingLocale() {
        Locale locale = Locale.forLanguageTag(nonExistentLocale);

        String actual = contentService.getText(indexPageTestContentDirectory, indexPageDescriptionFileName, locale);

        assertEquals(ErrorLocalizer.getLocalizedMessage(DESCRIPTION_NOT_AVAILABLE), actual);
    }

    @Test
    public void testGetTextWithNonExistingKey() {
        Locale locale = Locale.forLanguageTag(defaultLocale);

        String actual = contentService.getText(indexPageTestContentDirectory, invalidDirectory, locale);

        assertEquals(ErrorLocalizer.getLocalizedMessage(DESCRIPTION_NOT_AVAILABLE), actual);
    }

}