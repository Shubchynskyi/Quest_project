package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.constant.Key;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.javarush.quest.shubchynskyi.constant.Key.PATH_IMAGES;
import static com.javarush.quest.shubchynskyi.constant.Key.PATH_IMAGES_TEMP;

@Controller
@RequestMapping({PATH_IMAGES, PATH_IMAGES_TEMP})
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