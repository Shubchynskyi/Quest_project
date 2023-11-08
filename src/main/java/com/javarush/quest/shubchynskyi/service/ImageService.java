package com.javarush.quest.shubchynskyi.service;


import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Service
public class ImageService {

    private final Path imagesFolder;

    @Autowired
    public ImageService(@Value("${app.images-directory}") String imagesDirectory) throws IOException {
        this.imagesFolder = Paths.get(imagesDirectory);
        Files.createDirectories(this.imagesFolder);
    }

    public Path getImagePath(String filename) {
        Path potentialPath = imagesFolder.resolve(filename);
        if (Files.exists(potentialPath)) {
            return potentialPath;
        }

        for (String ext : Key.EXTENSIONS) {
            Path pathWithExtension = imagesFolder.resolve(filename + ext);
            if (Files.exists(pathWithExtension)) {
                return pathWithExtension;
            }
        }
        // TODO добавить логирование для случаев, когда файл не найден
        return imagesFolder.resolve(Key.NO_IMAGE_JPG);
    }

    public static final String ERROR_FILE_NULL_OR_EMPTY = "File is null or empty";
    public static final String ERROR_ORIGINAL_FILENAME_EMPTY = "Original filename is null or empty";
    public static final String ERROR_FILE_PATH_NOT_EXIST = "File path does not exist";


//    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
//        if (file == null || file.isEmpty()) {
//            throw new AppException(ERROR_FILE_NULL_OR_EMPTY);
//        }
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null || originalFilename.trim().isEmpty()) {
//            throw new AppException(ERROR_ORIGINAL_FILENAME_EMPTY);
//        }
//        try {
//            try {
//                isValid(file);
//            } catch (NullPointerException e) {
//                throw new AppException("NPE");
//            }
//            // Вы можете добавить логирование перед каждым throw, если это необходимо
//            return processFileUpload(file.getInputStream(), originalFilename, imageId, isTemporary);
//        } catch (IOException e) {
//            // Логирование ошибки e
//            throw new AppException(IMAGE_UPLOAD_ERROR, e);
//        } catch (IllegalArgumentException e) {
//            // Логирование ошибки e
//            throw new AppException(INVALID_FILE_TYPE, e);
//        }
//    }
//
//    public void uploadFromExistingFile(String fileName, String imageId) {
//        if (fileName == null || fileName.trim().isEmpty()) {
//            throw new AppException(ERROR_FILE_NULL_OR_EMPTY);
//        }
//        Path filePath = getImagePath(fileName);
//        if (!Files.exists(filePath)) {
//            throw new AppException(ERROR_FILE_PATH_NOT_EXIST);
//        }
//        try (InputStream inputStream = Files.newInputStream(filePath)) {
//            isValid(filePath); // Проверка уже произведена в getImagePath, можно опустить, если проверка на mimeType не нужна
//            processFileUpload(inputStream, filePath.getFileName().toString(), imageId, false);
//        } catch (IOException e) {
//            // Логирование ошибки e
//            throw new AppException(IMAGE_UPLOAD_ERROR, e);
//        }
//    }

    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
        try {
            if (!file.isEmpty()) {
                isValid(file);
                return processFileUpload(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()), imageId, isTemporary);
            }
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR);
        } catch (IllegalArgumentException e) {
            throw new AppException(INVALID_FILE_TYPE);
        }
        return NO_IMAGE_JPG;
    }
    // TODO в этих двух методах проверить работу с ошибками в исключениях
    public void uploadFromExistingFile(String fileName, String imageId) {
        try {
            Path filePath = getImagePath(fileName);
            if (Files.exists(filePath)) {
                isValid(filePath);
                processFileUpload(
                        Files.newInputStream(filePath),
                        filePath.getFileName().toString(),
                        imageId,
                        false
                );
            }
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR);
        }
    }

    private String processFileUpload(InputStream data, String filename, String imageId, boolean isTemporary) throws IOException {
        String ext = filename.substring(filename.lastIndexOf("."));
        String newFileName = isTemporary ?
                generateTemporaryFileName(imageId, ext) :
                imageId + ext;
        deleteOldFiles(imageId);
        uploadImageInternal(newFileName, data);
        return newFileName;
    }

    private String generateTemporaryFileName(String imageId, String ext) {
        return "temp_" + System.currentTimeMillis() + "_" + imageId + ext;
    }

    // TODO пользователю нужно как-то сообщать об ошибках в форму ввода изображения
    private boolean isValid(String filePath) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(filePath));
        return mimeType != null && Key.ALLOWED_MIME_TYPES.contains(mimeType);
    }

    //TODO обработать
    public boolean isValid(MultipartFile file) throws IOException {
        return isValid(Objects.requireNonNull(file.getOriginalFilename()));
    }

    //TODO обработать
    private boolean isValid(Path filePath) throws IOException {
        return isValid(filePath.toString());
    }

    private void deleteOldFiles(String filename) {
        Key.EXTENSIONS.stream()
                .map(ext -> imagesFolder.resolve(filename + ext))
                .forEach(path -> {
                    try {
                        if (Files.exists(path)) {
                            Files.delete(path);
                            // TODO Логирование успешного удаления
                            //logger.info("File deleted successfully: {}", path);
                        }
                    } catch (IOException e) {
                        // TODO Логирование исключения, возможно, с подробностями о файле
//                        logger.error("Error deleting file: " + path, e);
                        throw new AppException("Error deleting file: " + path, e);
                    }
                });
    }

//    private void deleteOldFiles(String filename) {
//        Key.EXTENSIONS.stream()
//                .map(ext -> imagesFolder.resolve(filename + ext))
//                .filter(Files::exists)
//                .forEach(p -> {
//                    try {
//                        Files.deleteIfExists(p);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//    }

    private void uploadImageInternal(String name, InputStream data) throws IOException {
        try (data) {
            if (data.available() > 0) {
                Files.copy(data, imagesFolder.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static final long THIRTY_MINUTES_IN_MILLIS = 1800000L;
    private static final Pattern TEMP_FILE_PATTERN = Pattern.compile("^temp_(\\d+)_.*$");

    public void deleteExpiredTempFiles() {
        File folder = imagesFolder.toFile();
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                Matcher matcher = TEMP_FILE_PATTERN.matcher(file.getName());
                if (matcher.matches()) {
                    try {
                        long timestamp = Long.parseLong(matcher.group(1));
                        if (currentTime - timestamp > THIRTY_MINUTES_IN_MILLIS) {
                            if (!file.delete()) {
                                // Логирование ошибки
                            } else {
                                // Логирование успешного удаления
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Логирование ошибки
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")  // каждый час
    public void scheduledDeleteExpiredFiles() {
        deleteExpiredTempFiles();
    }


}
