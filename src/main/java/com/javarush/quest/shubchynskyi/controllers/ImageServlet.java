package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.config.ClassInitializer;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet(name = "ImageServlet", value = Go.IMAGES_ALL)
public class ImageServlet extends HttpServlet {
    private final ImageService imageService = ClassInitializer.getBean(ImageService.class);

    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        String target = req.getContextPath() + Go.IMAGES;
        String nameImage = requestURI.replace(target, Key.REGEX_EMPTY_STRING);
        Path path = imageService.getImagePath(nameImage);
        Files.copy(path, resp.getOutputStream());
    }
}
