package com.javarush.quest.shubchynskyi.service;


import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.IMAGE_UPLOAD_IO_ERROR;
import static com.javarush.quest.shubchynskyi.constant.Key.INVALID_FILE_TYPE;

@Service
public class ImageService {

    private final Path imagesFolder;
    private final Path tempFilesDir;

    @Autowired
    public ImageService(@Value("${app.images-directory}") String imagesDirectory) throws IOException {
        this.imagesFolder = Paths.get(imagesDirectory);
        this.tempFilesDir = this.imagesFolder.resolve(Key.PREFIX_FOR_TEMP_IMAGES);

        Files.createDirectories(this.imagesFolder);
        Files.createDirectories(this.tempFilesDir);
    }

    public Path getImagePath(String filename) {
        if (filename == null) {
            throw new AppException(Key.FILE_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            if (filename.trim().isEmpty()) {
                throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED);
            }

            boolean isTemporary = filename.startsWith(Key.PREFIX_FOR_TEMP_IMAGES);
            Path rootDir = isTemporary ? tempFilesDir : imagesFolder;
            Path resolvedPath = rootDir.resolve(filename).normalize().toAbsolutePath();

            // Проверка, что путь не выходит за пределы rootDir
            if (!resolvedPath.startsWith(rootDir.toAbsolutePath())) {
                throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED);
            }

            if (Files.exists(resolvedPath)) {
                return resolvedPath;
            }

            for (String ext : Key.ALLOWED_EXTENSIONS) {
                Path pathWithExtension = resolvedPath.getParent().resolve(resolvedPath.getFileName().toString() + ext);
                if (Files.exists(pathWithExtension)) {
                    return pathWithExtension;
                }
            }

            return rootDir.resolve(Key.NO_IMAGE_JPG);
        } catch (InvalidPathException e) {
            throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED, e);
        }
    }

    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
        if (file == null || file.isEmpty()) {
            // logger.info("No new file provided, using default image.");
            return null;
        }

        if (isPathUnsecure(imageId)) {
            throw new AppException(Key.THE_FILE_PATH_IS_INSECURE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new AppException(Key.ORIGINAL_FILENAME_IS_NULL_OR_EMPTY);
        }

        try {
            if (!isValid(file)) {
                throw new AppException(INVALID_FILE_TYPE);
            }
            return processFileUpload(file.getInputStream(), originalFilename, imageId, isTemporary);
        } catch (IOException e) {
            throw new AppException(IMAGE_UPLOAD_IO_ERROR);
        }
    }

    public void uploadFromExistingFile(String fileName, String imageId) {
        if (fileName == null || fileName.isEmpty()) {
            throw new AppException(Key.FILE_NAME_IS_NULL_OR_EMPTY);
        }

        if (isPathUnsecure(imageId)) {
            throw new AppException(Key.THE_FILE_PATH_IS_INSECURE);
        }

        try {
            Path filePath = getImagePath(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(Key.FILE_DOES_NOT_EXIST);
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
            throw new AppException(IMAGE_UPLOAD_IO_ERROR);
        }
    }

    private String processFileUpload(InputStream data, String filename, String imageId, boolean isTemporary) throws IOException {
        String ext = filename.substring(filename.lastIndexOf("."));
        String newFileName;

        if (isTemporary) {
            newFileName = generateTemporaryFileName(imageId) + ext;
        } else {
            newFileName = imageId + ext;
            deleteOldFiles(imageId);
        }

        Path uploadPath = isTemporary ? tempFilesDir : imagesFolder;
        uploadImageInternal(uploadPath, newFileName, data);
        return newFileName;
    }

    private void uploadImageInternal(Path dirPath, String name, InputStream data) {
        Path targetPath = dirPath.resolve(name);
        try {
            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO log
            throw new AppException(Key.ERROR_UPLOADING_IMAGE + name, e);
        }
    }

    // todo prefix to settings
    private String generateTemporaryFileName(String imageId) {
        return "temp_"
                + System.currentTimeMillis()
                + "_"
                + UUID.randomUUID()
                + "_" + imageId;
    }

    public boolean isValid(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (file.isEmpty() || originalFilename == null || !originalFilename.contains(".")) {
            return false;
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Key.ALLOWED_EXTENSIONS.contains(extension)) {
            return false;
        }

        Path filePath = null;
        try {
            String uniqueFilename = generateTemporaryFileName(file.getOriginalFilename());
            filePath = tempFilesDir.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String mimeType = Files.probeContentType(filePath);
            return mimeType != null && Key.ALLOWED_MIME_TYPES.contains(mimeType);
        } catch (IOException e) {
            // Логирование ошибки с уровнем WARN
            return false;
        } finally {
            // Попытка удалить временный файл, если он был создан
            try {
                if (filePath != null) Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Логирование ошибки при удалении файла
            }
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
//            logger.warn("...");
            return false;
        }
    }

    private boolean isPathUnsecure(String imageId) {
        if (imageId == null
                || imageId.length() > Key.MAX_LENGTH
                || !imageId.matches("^[a-zA-Z0-9_.-]+$")
                || imageId.contains("..")) {
//            logger.warn("Unsafe path: {}", imageId);
            return true;
        }
        return false;
    }

    public void deleteOldFiles(String imageId) {
        for (String ext : Key.ALLOWED_EXTENSIONS) {
            Path path = imagesFolder.resolve(imageId + ext);
            if (Files.exists(path)) {
                if (!tryDeleteFile(path)) {
//  todo                  logger.warn("Не удалось удалить файл: " + path);
                }
                break;
            }
        }
    }

    private boolean tryDeleteFile(Path path) {
        try {
            Files.delete(path);
            // logger.info("File deleted successfully: {}", path);
            return true;
        } catch (IOException e) {
            // logger.error("Error deleting file: " + path, e);
            return false;
        }
    }

    @Scheduled(cron = "0 */10 * * * ?")  // 10 min
    public void scheduledDeleteExpiredFiles() {
        deleteExpiredTempFiles();
    }

    private void deleteExpiredTempFiles() {
        try (Stream<Path> paths = Files.list(tempFilesDir)) {
            long currentTime = System.currentTimeMillis();

            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        Matcher matcher = Key.TEMP_FILE_PATTERN.matcher(fileName);
                        if (matcher.matches()) {
                            try {
                                long timestamp = Long.parseLong(matcher.group(1));
                                if (currentTime - timestamp > Key.TIME_TO_DELETE_IN_MILLIS) {
                                    try {
                                        Files.delete(file);
                                        // TODO log success
                                    } catch (IOException e) {
                                        // TODO log error
                                    }
                                }
                            } catch (NumberFormatException e) {
                                // TODO log error parsing time
                            }
                        }
                    });
        } catch (IOException e) {
            // TODO log error
        }
    }


}
