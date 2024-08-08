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

    @Autowired
    private MockMvc mockMvc;

    private String getImageUrl(String imageName) {
        return basePath + imageName;
    }

    @Test
    public void getImage_WhenImageFound_ShouldReturnImage() throws Exception {
        mockMvc.perform(get(getImageUrl(testImageName)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(testImageContentType));
    }

    @Test
    public void getImage_WhenImageHasNoExtension_ShouldHandleGracefully() throws Exception {
        mockMvc.perform(get(getImageUrl(testImageNoExt)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(testImageContentType));
    }

    @Test
    public void getImage_WhenImageNotFound_ShouldReturnDefault() throws Exception {
        mockMvc.perform(get(getImageUrl(testImageNonexistent)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG_VALUE));
    }
}