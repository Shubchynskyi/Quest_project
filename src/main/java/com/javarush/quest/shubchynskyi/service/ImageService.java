package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.config.Config;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public enum ImageService {

    IMAGE_SERVICE;
    private final Config config = Config.CONFIG;

    private final Path imagesFolder = config.WEB_INF.resolve(Key.IMAGES_FOLDER);

    @SneakyThrows
    ImageService() {
        Files.createDirectories(imagesFolder);
    }

    @SneakyThrows
    public Path getImagePath(String filename) {
        return Key.EXTENSIONS.stream()
                .map(ext -> imagesFolder.resolve(filename + ext))
                .filter(Files::exists)
                .findAny()
                .orElse(imagesFolder.resolve(Key.NO_IMAGE_PNG));
    }

    public void uploadImage(HttpServletRequest request, String imageId) throws ServletException, IOException {
        Part data = request.getPart(Key.PART_NAME);
        if (Objects.nonNull(data) && data.getInputStream().available() > 0) {
            String filename = data.getSubmittedFileName();
            String ext = filename.substring(filename.lastIndexOf("."));
            deleteOldFiles(imageId);
            filename = imageId + ext;
            uploadImageInternal(filename, data.getInputStream());
        }
    }

    private void deleteOldFiles(String filename) {
        Key.EXTENSIONS.stream()
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
