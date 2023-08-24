package com.javarush.quest.shubchynskyi.service;


import com.javarush.quest.shubchynskyi.constant.Key;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class ImageService {
    public static final String INVALID_FILE_TYPE = "Invalid file type: ";  // TODO
    private Path imagesFolder;

    @Value("${app.images-directory}")
    private String imagesDirectory;

    @PostConstruct
    public void init() throws IOException {
        imagesFolder = Paths.get(imagesDirectory);
        Files.createDirectories(imagesFolder);
    }

    public Path getImagePath(String filename) {
        return Key.EXTENSIONS.stream()
                .map(ext -> imagesFolder.resolve(filename + ext))
                .filter(Files::exists)
                .findAny()
                .orElse(imagesFolder.resolve(Key.NO_IMAGE_PNG));
    }

    public void uploadImage(MultipartFile file, String imageId) throws IOException {
        if (!file.isEmpty()) {
            validate(file);
            String filename = file.getOriginalFilename();
            String ext = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
            deleteOldFiles(imageId);
            filename = imageId + ext;
            uploadImageInternal(filename, file.getInputStream());
        }
    }

    private void validate(MultipartFile file) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(Objects.requireNonNull(file.getOriginalFilename())));
        if (mimeType == null || !Key.ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(INVALID_FILE_TYPE + mimeType);
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

    private void uploadImageInternal(String name, InputStream data) throws IOException {
        try (data) {
            if (data.available() > 0) {
                Files.copy(data, imagesFolder.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
