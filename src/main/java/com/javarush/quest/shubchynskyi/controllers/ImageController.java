package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/{imageName}")
    public void getImage(@PathVariable("imageName") String imageName, HttpServletResponse response) throws IOException {
        Path imagePath = imageService.getImagePath(imageName);
        Files.copy(imagePath, response.getOutputStream());
        response.getOutputStream().flush();
    }
}