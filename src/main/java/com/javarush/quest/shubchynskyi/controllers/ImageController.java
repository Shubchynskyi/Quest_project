package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.config.ImageProperties;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.IMAGE_NOT_FOUND;

@Slf4j
@Controller
@RequestMapping({PATH_IMAGES, PATH_IMAGES_TEMP})
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageProperties imageProperties;

    @GetMapping(value = PATH_IMAGE_NAME)
    public void getImage(@PathVariable(PARAM_IMAGE_NAME) String imageName, HttpServletResponse response) throws IOException {
        try {
            Path imagePath = imageService.getImagePath(imageName);
            String mimeType = determineMimeType(imagePath);
            response.setContentType(mimeType);
            Files.copy(imagePath, response.getOutputStream());
            response.getOutputStream().flush();
            log.info("Image '{}' served successfully with MIME type '{}'.", imageName, mimeType);
        } catch (IOException e) {
            log.warn("Failed to serve image '{}': {}", imageName, e.getMessage());
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(IMAGE_NOT_FOUND);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, localizedMessage);
        }
    }

    private String determineMimeType(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return imageProperties.getExtensionToMimeType().entrySet().stream()
                .filter(entry -> fileName.endsWith(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(imageProperties.getDefaultMimeType());
    }

}