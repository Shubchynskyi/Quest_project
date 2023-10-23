package com.javarush.quest.shubchynskyi.service;


import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Service
public class ImageService {

    private Path imagesFolder;

    @Value("${app.images-directory}")
    private String imagesDirectory;

    @PostConstruct
    public void init() throws IOException {
        imagesFolder = Paths.get(imagesDirectory);
        Files.createDirectories(imagesFolder);
    }

//    public Path getImagePath(String filename) {
//        return Key.EXTENSIONS.stream()
//                .map(ext -> imagesFolder.resolve(filename + ext))
//                .filter(Files::exists)
//                .findAny()
//                .orElse(imagesFolder.resolve(Key.NO_IMAGE_JPG));
//    }

    public Path getImagePath(String filename) {
        // Если имя файла уже содержит расширение, то возвращаем путь к этому файлу
        for (String ext : Key.EXTENSIONS) {
            if (filename.endsWith(ext)) {
                Path potentialPath = imagesFolder.resolve(filename);
                if (Files.exists(potentialPath)) {
                    return potentialPath;
                }
            }
        }

        // Иначе ищем существующий файл, добавляя разные расширения
        for (String ext : Key.EXTENSIONS) {
            Path potentialPath = imagesFolder.resolve(filename + ext);
            if (Files.exists(potentialPath)) {
                return potentialPath;
            }
        }

        return imagesFolder.resolve(Key.NO_IMAGE_JPG);
    }

//    public String uploadImage(MultipartFile file, String imageId, boolean isTemporary) {
//        try {
//            if (!file.isEmpty()) {
//                validate(file);
//                String filename = file.getOriginalFilename();
//                String ext = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
//
//                if (isTemporary) {
//                    filename = "temp_" + System.currentTimeMillis() + "_" + imageId + ext;
//                    System.out.println("Saving temporary image as: " + filename);  // TODO Для отладки
//                } else {
//                    filename = imageId + ext;
//                }
//
//                deleteOldFiles(imageId);
//                uploadImageInternal(filename, file.getInputStream());
//
//                return filename;
//            }
//        } catch (IOException | NullPointerException e) {
//            throw new AppException(IMAGE_UPLOAD_ERROR);
//        } catch (IllegalArgumentException e) {
//            throw new AppException(INVALID_FILE_TYPE);
//        }
//
//        return NO_IMAGE_JPG;
//    }

    public String uploadImage(MultipartFile file, String imageId, boolean isTemporary) {
        try {
            if (!file.isEmpty()) {
                validate(file);
                String filename = file.getOriginalFilename();

                String ext = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));

                if (isTemporary) {
                    filename = "temp_" + System.currentTimeMillis() + "_" + imageId + ext;
                } else {
                    filename = imageId + ext;
                }
                deleteOldFiles(imageId);
                uploadImageInternal(filename, file.getInputStream());

                return filename;
            }
        } catch (IOException | NullPointerException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR);
        } catch (IllegalArgumentException e) {
            throw new AppException(INVALID_FILE_TYPE);
        }

        return NO_IMAGE_JPG;
    }

    public String uploadImage(String fileName, String imageId, boolean isTemporary) {
        try {
            Path filePath = getImagePath(fileName);
            if (Files.exists(filePath)) {
                validate(filePath); // Добавленная валидация

                String filename = filePath.getFileName().toString();
                String ext = filename.substring(filename.lastIndexOf("."));

                if (isTemporary) {
                    filename = "temp_" + System.currentTimeMillis() + "_" + imageId + ext;
                } else {
                    filename = imageId + ext;
                }

                deleteOldFiles(imageId);
                uploadImageInternal(filename, Files.newInputStream(filePath));

                return filename;
            }
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR);
        } catch (IllegalArgumentException e) {
            throw new AppException(INVALID_FILE_TYPE);
        }

        return NO_IMAGE_JPG;
    }


    private void validate(MultipartFile file) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(Objects.requireNonNull(file.getOriginalFilename())));
        if (mimeType == null || !Key.ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(INVALID_FILE_TYPE + ": " + mimeType);
        }
    }

    private void validate(Path filePath) throws IOException {
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null || !Key.ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(INVALID_FILE_TYPE + ": " + mimeType);
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

    public void deleteExpiredTempFiles() {
        File folder = new File(imagesDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String name = file.getName();
                if (name.startsWith("temp_")) {
                    String[] parts = name.split("_");
                    long timestamp;
                    try {
                        timestamp = Long.parseLong(parts[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if (currentTime - timestamp > 60000) {  // устаревшие через 1 минуту
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.out.println();
                            System.err.println("Не удалось удалить файл: " + file.getName());
                            System.out.println();
                        } else {
                            System.out.println();
                            System.err.println("Файл удален: " + file.getName());
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")  // каждую минуту
    public void scheduledDeleteExpiredFiles() {
        deleteExpiredTempFiles();
    }


}
