package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Controller
@RequestMapping({PATH_IMAGES, PATH_IMAGES_TEMP})
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


    //todo move to a separate class
    private static final Map<String, String> extensionToMimeType = new HashMap<>();
    static {
        extensionToMimeType.put(".jpg", "image/jpeg");
        extensionToMimeType.put(".jpeg", "image/jpeg");
        extensionToMimeType.put(".png", "image/png");
        extensionToMimeType.put(".bmp", "image/bmp");
        extensionToMimeType.put(".gif", "image/gif");
        extensionToMimeType.put(".webp", "image/webp");
    }

    @GetMapping(value = PATH_IMAGE_NAME)
    public void getImage(@PathVariable(PARAM_IMAGE_NAME) String imageName, HttpServletResponse response) throws IOException {
        Path imagePath = imageService.getImagePath(imageName);
        String mimeType = determineMimeType(imagePath);
        response.setContentType(mimeType);
        Files.copy(imagePath, response.getOutputStream());
        response.getOutputStream().flush();
    }

    private String determineMimeType(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return extensionToMimeType.entrySet().stream()
                .filter(entry -> fileName.endsWith(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("application/octet-stream");  // Default MIME type if no match
    }
}