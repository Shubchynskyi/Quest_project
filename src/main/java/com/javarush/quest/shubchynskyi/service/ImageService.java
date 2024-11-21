package com.javarush.quest.shubchynskyi.service;


import com.javarush.quest.shubchynskyi.config.ImageProperties;
import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class ImageService {

    @Value("${app.resources.images.temp.prefix}")
    private String imagesTempPrefix;

    private final ImageProperties imageProperties;

    private final Path imagesFolder;
    private final Path tempFilesDir;

    @Autowired
    public ImageService(@Value("${app.directories.images}") String imagesDirectory,
                        ImageProperties imageProperties

    ) throws IOException {
        this.imageProperties = imageProperties;
        this.imagesFolder = Paths.get(imagesDirectory);
        this.tempFilesDir = this.imagesFolder.resolve(imageProperties.getPrefixForTempImages());

        Files.createDirectories(this.imagesFolder);
        Files.createDirectories(this.tempFilesDir);
        log.info("Initialized ImageService with imagesFolder: {} and tempFilesDir: {}", imagesFolder, tempFilesDir);
    }

    public Path getImagePath(String filename) {
        if (filename == null) {
            log.warn("Filename is null or empty.");
            throw new AppException(Key.FILE_NAME_IS_NULL_OR_EMPTY);
        }

        try {
            if (filename.trim().isEmpty()) {
                log.warn("Attempted access with an empty filename.");
                throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED);
            }

            boolean isTemporary = filename.startsWith(imageProperties.getPrefixForTempImages());
            Path rootDir = isTemporary ? tempFilesDir : imagesFolder;
            Path resolvedPath = rootDir.resolve(filename).normalize();

            if (!resolvedPath.startsWith(rootDir)) {
                log.warn("Invalid file path access attempt: {}", filename);
                throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED);
            }

            if (Files.exists(resolvedPath)) {
                return resolvedPath;
            }

            for (String ext : imageProperties.getAllowedExtensions()) {
                Path pathWithExtension = resolvedPath.getParent().resolve(resolvedPath.getFileName().toString() + ext);
                if (Files.exists(pathWithExtension)) {
                    return pathWithExtension;
                }
            }

            log.info("File not found, returning default image path.");
            return rootDir.resolve(imageProperties.getNoImageFilename());
        } catch (InvalidPathException e) {
            log.warn("Invalid path exception for filename: {}", filename, e);
            throw new SecurityException(Key.INVALID_FILE_PATH_ACCESS_DENIED, e);
        }
    }

    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
        if (file == null || file.isEmpty()) {
            log.info("No file provided for upload. Using default image.");
            return null;
        }

        if (isPathUnsecure(imageId)) {
            throw new AppException(Key.THE_FILE_PATH_IS_INSECURE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            log.warn("Original filename is null or empty.");
            throw new AppException(Key.ORIGINAL_FILENAME_IS_NULL_OR_EMPTY);
        }

        try {
            if (!isValid(file)) {
                log.warn("Invalid file: {}", originalFilename);
                throw new AppException(Key.INVALID_FILE_TYPE);
            }
            log.info("Uploading file: {}", originalFilename);
            return processFileUpload(file.getInputStream(), originalFilename, imageId, isTemporary);
        } catch (IOException e) {
            log.warn("IO exception during file upload: {}", originalFilename, e);
            throw new AppException(Key.IMAGE_UPLOAD_IO_ERROR);
        }
    }

    public void uploadFromExistingFile(String fileName, String imageId) {
        if (fileName == null || fileName.isEmpty()) {
            log.warn("File name is null or empty.");
            throw new AppException(Key.FILE_NAME_IS_NULL_OR_EMPTY);
        }

        if (isPathUnsecure(imageId)) {
            throw new AppException(Key.THE_FILE_PATH_IS_INSECURE);
        }

        try {
            Path filePath = getImagePath(fileName);
            if (!Files.exists(filePath)) {
                log.warn("File does not exist: {}", filePath);
                throw new AppException(Key.FILE_DOES_NOT_EXIST);
            }

            if (!isValid(filePath)) {
                log.warn("Invalid filePath for file: {}", filePath);
                throw new AppException(Key.INVALID_FILE_TYPE);
            }

            log.info("Uploading existing file: {}", filePath);
            processFileUpload(
                    Files.newInputStream(filePath),
                    filePath.getFileName().toString(),
                    imageId,
                    false
            );
        } catch (IOException e) {
            log.warn("IO exception during existing file upload: {}", fileName, e);
            throw new AppException(Key.IMAGE_UPLOAD_IO_ERROR);
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
        log.info("File uploaded successfully: {}", newFileName);
        return newFileName;
    }

    private void uploadImageInternal(Path dirPath, String name, InputStream data) {
        Path targetPath = dirPath.resolve(name);
        try {
            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Image '{}' uploaded to '{}'", name, dirPath);
        } catch (IOException e) {
            log.warn("Error uploading image '{}'. Target directory: '{}'", name, dirPath, e);
            throw new AppException(Key.ERROR_UPLOADING_IMAGE + name, e);
        }
    }

    private String generateTemporaryFileName(String imageId) {
        return imagesTempPrefix + System.currentTimeMillis() +
                "_" + UUID.randomUUID() +
                "_" + imageId;
    }

    public boolean isValid(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (file.isEmpty() || originalFilename == null || !originalFilename.contains(".")) {
            log.warn("File is empty or has invalid filename: {}", originalFilename);
            return false;
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!imageProperties.getAllowedExtensions().contains(extension)) {
            log.warn("Invalid file extension: {}", extension);
            return false;
        }

        Path filePath = null;
        try {
            String uniqueFilename = generateTemporaryFileName(originalFilename);
            filePath = tempFilesDir.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null || !imageProperties.getAllowedMimeTypes().contains(mimeType)) {
                log.warn("Invalid MIME type: {}", mimeType);
                return false;
            }

            return true;
        } catch (IOException e) {
            log.warn("IOException during file validation: {}", originalFilename, e);
            return false;
        } finally {
            try {
                if (filePath != null) Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Error deleting temporary file during validation: {}", filePath, e);
            }
        }
    }

    public boolean isValid(Path filePath) {
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            log.warn("File does not exist or is not readable: {}", filePath);
            return false;
        }

        try {
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null || !imageProperties.getAllowedMimeTypes().contains(mimeType)) {
                log.warn("Invalid MIME type for file: {}", mimeType);
                return false;
            }

            return true;
        } catch (IOException e) {
            log.warn("IOException during file validation: {}", filePath, e);
            return false;
        }
    }

    private boolean isPathUnsecure(String imageId) {
        if (imageId == null || imageId.length() > Key.MAX_LENGTH || !imageId.matches("^[a-zA-Z0-9_.-]+$") || imageId.contains("..")) {
            log.warn("Unsecure image ID: {}", imageId);
            return true;
        }
        return false;
    }

    public void deleteOldFiles(String imageId) {
        for (String ext : imageProperties.getAllowedExtensions()) {
            Path path = imagesFolder.resolve(imageId + ext);
            if (Files.exists(path)) {
                if (!tryDeleteFile(path)) {
                    log.warn("Failed to delete old file: {}", path);
                }
                break;
            }
        }
    }

    private boolean tryDeleteFile(Path path) {
        try {
            Files.delete(path);
            log.info("File deleted successfully: {}", path);
            return true;
        } catch (IOException e) {
            log.warn("Error deleting file: {}", path, e);
            return false;
        }
    }

    public int getMaxFileSize() {
        return imageProperties.getMaxFileSize();
    }

    @Scheduled(cron = "0 */10 * * * ?")  // Every 10 minutes
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
                                if (currentTime - timestamp > imageProperties.getTimeToDeleteInMillis()) {
                                    try {
                                        Files.delete(file);
                                        log.info("Expired temporary file deleted: {}", file);
                                    } catch (IOException e) {
                                        log.warn("Error deleting expired temporary file: {}", file, e);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                log.error("Error parsing timestamp from file name: {}", fileName, e);
                            }
                        }
                    });
        } catch (IOException e) {
            log.warn("Error listing temporary files in directory: {}", tempFilesDir, e);
        }
    }
}