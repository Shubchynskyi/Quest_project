package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.images.base-path}")
    private String basePath;
    @Value("${app.images.test-image.name}")
    private String testImageName;
    @Value("${app.images.test-image.no-ext}")
    private String testImageNoExt;
    @Value("${app.images.test-image.nonexistent}")
    private String testImageNonexistent;
    @Value("${app.images.test-image.content-type}")
    private String testImageContentType;

    private void performGetRequestAndExpectStatusAndContentType(String imageName, String expectedContentType) throws Exception {
        mockMvc.perform(get(basePath + imageName))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(expectedContentType));
    }

    @Test
    void whenGetImageFound_ThenShouldReturnImage() throws Exception {
        performGetRequestAndExpectStatusAndContentType(testImageName, testImageContentType);
    }

    @Test
    void whenGetImageWithNoExtension_ThenShouldHandleGracefully() throws Exception {
        performGetRequestAndExpectStatusAndContentType(testImageNoExt, testImageContentType);
    }

    @Test
    void whenGetImageNotFound_ThenShouldReturnDefault() throws Exception {
        performGetRequestAndExpectStatusAndContentType(testImageNonexistent, MediaType.IMAGE_JPEG_VALUE);
    }
}