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
        Path resolvedPath = imagesFolder.resolve(filename).normalize();

        if (!resolvedPath.startsWith(imagesFolder)) {
            throw new SecurityException("Invalid file path, access denied");
        }

        if (Files.exists(resolvedPath)) {
            return resolvedPath;
        }

        for (String ext : Key.EXTENSIONS) {
            Path pathWithExtension = resolvedPath.getParent().resolve(resolvedPath.getFileName().toString() + ext);
            if (Files.exists(pathWithExtension)) {
                return pathWithExtension;
            }
        }

        // TODO: Добавить логирование для случаев, когда файл не найден
        return imagesFolder.resolve(Key.NO_IMAGE_JPG);
    }


    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is null or empty");
        }

        if (!isPathSecure(imageId)) {
            throw new AppException("The file path is insecure");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new AppException("Original filename is null or empty");
        }

        try {
            if (!isValid(file)) {
                throw new AppException(INVALID_FILE_TYPE);
            }
            return processFileUpload(file.getInputStream(), originalFilename, imageId, isTemporary);
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR, e);
        }
    }

    public void uploadFromExistingFile(String fileName, String imageId) {
        if (fileName == null || fileName.isEmpty()) {
            throw new AppException("File name is null or empty");
        }

        if (!isPathSecure(imageId)) {
            throw new AppException("The file path is insecure");
        }

        try {
            Path filePath = getImagePath(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException("File does not exist");
            }

            if (!isValid(filePath)) {
                throw new AppException(INVALID_FILE_TYPE);
            }

            processFileUpload(
                    Files.newInputStream(filePath),
                    filePath.getFileName().toString(),
                    imageId,
                    false
            );
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR, e);
        }
    }

    private String processFileUpload(InputStream data, String filename, String imageId, boolean isTemporary) throws IOException {
        if (!filename.contains(".")) {
            throw new AppException("Filename does not contain an extension");
        }

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

    public boolean isValid(MultipartFile file) {
        // Проверка на непустое содержимое и наличие файла не требует чтения MIME-типа.
        if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return false;
        }

        try {
            // Создаем временный файл для получения Path.
            Path tempFile = Files.createTempFile("upload_", file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            // Проверяем MIME-тип.
            boolean valid = isValid(tempFile);

            // Удаляем временный файл.
            Files.delete(tempFile);

            return valid;
        } catch (IOException e) {
            // Логирование ошибки с уровнем WARN.
            return false;
        }
    }

    public boolean isValid(Path filePath) {
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            return false;
        }

        try {
            String mimeType = Files.probeContentType(filePath);
            return mimeType != null && Key.ALLOWED_MIME_TYPES.contains(mimeType);
        } catch (IOException e) {
            // Логирование ошибки с уровнем WARN.
            return false;
        }
    }



    private boolean isPathSecure(String imageId) {
        return imageId != null && !imageId.contains("..");
    }

    private void deleteOldFiles(String imageId) {
        for (String ext : Key.EXTENSIONS) {
            Path path = imagesFolder.resolve(imageId + ext);
            if (Files.exists(path)) {
                tryDeleteFile(path);
            }
        }
    }

    private void tryDeleteFile(Path path) {
        try {
            Files.delete(path);
            // logger.info("File deleted successfully: {}", path);
        } catch (IOException e) {
            // logger.error("Error deleting file: " + path, e);
            throw new AppException("Error deleting file: " + path, e);
        }
    }

    private void uploadImageInternal(String name, InputStream data) throws IOException {
        Path targetPath = imagesFolder.resolve(name);
        long copiedBytes = Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
        if (copiedBytes == 0) {
            throw new AppException("The file is empty and cannot be uploaded.");
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
