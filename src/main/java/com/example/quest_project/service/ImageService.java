package com.example.quest_project.service;

import com.example.quest_project.config.Config;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public enum ImageService {

    IMAGE_SERVICE;
    private final Config config = Config.CONFIG;

    public static final String IMAGES_FOLDER = "images"; // расположение всех аватаров
    public static final String PART_NAME = "image";
//    public static final String FILENAME_PREFIX = "image-";
    public static final String NO_IMAGE_PNG = "no-image.png";   // аватар по умолчанию
    public static final List<String> EXTENSIONS = List.of(      // возможные расширения
            ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp"
    );

    private final Path imagesFolder = config.WEB_INF.resolve(IMAGES_FOLDER);

    @SneakyThrows
    ImageService() {
        Files.createDirectories(imagesFolder);      // в конструкторе создается папка
    }

    @SneakyThrows
    public Path getImagePath(String filename) {
        return EXTENSIONS.stream()
                .map(ext -> imagesFolder.resolve(filename + ext))
                .filter(Files::exists)
                .findAny()
                .orElse(imagesFolder.resolve(NO_IMAGE_PNG));
    }

    public void uploadImage(HttpServletRequest request, String imageId) throws ServletException, IOException {
        Part data = request.getPart(PART_NAME);
        if (Objects.nonNull(data) && data.getInputStream().available() > 0) {
            String filename = data.getSubmittedFileName();
            String ext = filename.substring(filename.lastIndexOf("."));
            deleteOldFiles(imageId);
            filename = imageId + ext;
            uploadImageInternal(filename, data.getInputStream());
        }
    }



    private void deleteOldFiles(String filename) {
        EXTENSIONS.stream()
                .map(ext -> imagesFolder.resolve(filename + ext))
                .filter(Files::exists)
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @SneakyThrows
    private void uploadImageInternal(String name, InputStream data) {
        try (data) {
            if (data.available() > 0) {
                Files.copy(data, imagesFolder.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
