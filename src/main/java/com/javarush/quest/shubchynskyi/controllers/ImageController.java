package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.util.constant.Key;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.javarush.quest.shubchynskyi.util.constant.Key.PATH_IMAGES;

@Controller
@RequestMapping(PATH_IMAGES)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = Key.PATH_IMAGE_NAME)
    public void getImage(@PathVariable(Key.PARAM_IMAGE_NAME) String imageName, HttpServletResponse response) throws IOException {
        Path imagePath = imageService.getImagePath(imageName);
        Files.copy(imagePath, response.getOutputStream());
        response.getOutputStream().flush();
    }
}