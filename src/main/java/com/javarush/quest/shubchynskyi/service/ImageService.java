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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.javarush.quest.shubchynskyi.constant.Key.IMAGE_UPLOAD_ERROR;
import static com.javarush.quest.shubchynskyi.constant.Key.INVALID_FILE_TYPE;

@Service
public class ImageService {

    private final long THIRTY_MINUTES_IN_MILLIS = 1800000L;
    private final Pattern TEMP_FILE_PATTERN = Pattern.compile("^temp_(\\d+)_.*$");

    private final Path imagesFolder;
    private final Path tempFilesDir;

    @Autowired
    public ImageService(@Value("${app.images-directory}") String imagesDirectory) throws IOException {
        this.imagesFolder = Paths.get(imagesDirectory);
//        this.tempFilesDir = imagesFolder;
        this.tempFilesDir = this.imagesFolder.resolve("temp");

        Files.createDirectories(this.imagesFolder);
        Files.createDirectories(this.tempFilesDir);
    }

    public Path getImagePath(String filename) {
        // Определение, является ли файл временным на основе префикса
        boolean isTemporary = filename.startsWith("temp_");

        // Выбор корневой директории в зависимости от того, временный ли это файл
        Path rootDir = isTemporary ? tempFilesDir : imagesFolder;

        Path resolvedPath = rootDir.resolve(filename).normalize();

        // Проверка, что путь файла начинается с корневой директории (защита от путей типа "../")
        if (!resolvedPath.startsWith(rootDir)) {
            throw new SecurityException("Invalid file path, access denied");
        }

        // Проверка существования файла
        if (Files.exists(resolvedPath)) {
            return resolvedPath;
        }

        // Попытка найти файл с учетом возможных расширений
        for (String ext : Key.ALLOWED_EXTENSIONS) {
            Path pathWithExtension = resolvedPath.getParent().resolve(resolvedPath.getFileName().toString() + ext);
            if (Files.exists(pathWithExtension)) {
                return pathWithExtension;
            }
        }

        // TODO: Добавить логирование для случаев, когда файл не найден
        return rootDir.resolve(Key.NO_IMAGE_JPG);
    }


    public String uploadFromMultipartFile(MultipartFile file, String imageId, boolean isTemporary) {
        // Если файл пустой, возвращаем null или путь к изображению по умолчанию
        if (file == null || file.isEmpty()) {
            // logger.info("No new file provided, using default image.");
            return null; // или возвращаем путь к изображению по умолчанию
        }

        // если путь не безопасный, то ошибка
        if (!isPathSecure(imageId)) {
            throw new AppException("The file path is insecure");
        }


        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new AppException("Original filename is null or empty");
        }

        try {
            if (!isValid(file)) { // если файл не валидный - исключение с ошибкой которую можно использовать
                throw new AppException(INVALID_FILE_TYPE);
            }
            return processFileUpload(file.getInputStream(), originalFilename, imageId, isTemporary);
        } catch (IOException e) {
            // если ошибка ввода-вывода, то исключение, которое используем дальше
            throw new AppException(IMAGE_UPLOAD_ERROR);
        }
    }

    // вызываем из контроллера если это временное изображение (уже сохраненное)
    public void uploadFromExistingFile(String fileName, String imageId) {
        // может ли быть пустым?.. если уже сохранен как временный
        // или это надо для RESTа например?
        if (fileName == null || fileName.isEmpty()) {
            throw new AppException("File name is null or empty");
        }

        // проверка пути, хотя тут файл уже должен быть сохранен. ?
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
            throw new AppException(IMAGE_UPLOAD_ERROR);
        }
    }

//    private String processFileUpload(InputStream data, String filename, String imageId, boolean isTemporary) throws IOException {
//        String ext = filename.substring(filename.lastIndexOf("."));
//        String newFileName = isTemporary ? generateTemporaryFileName(imageId) : imageId + ext;
//
//        deleteOldFiles(imageId);
//        uploadImageInternal(newFileName, data);
//        return newFileName;
//    }

    private String processFileUpload(InputStream data, String filename, String imageId, boolean isTemporary) throws IOException {
        String ext = filename.substring(filename.lastIndexOf("."));
        String newFileName;

        if (isTemporary) {
            newFileName = generateTemporaryFileName(imageId) + ext;
        } else {
            newFileName = imageId + ext;
            deleteOldFiles(imageId); // Удаление старых файлов для данного imageId
        }

        Path uploadPath = isTemporary ? tempFilesDir : imagesFolder;
        uploadImageInternal(uploadPath, newFileName, data);
        return newFileName;
    }

    private void uploadImageInternal(Path dirPath, String name, InputStream data) throws IOException {
        Path targetPath = dirPath.resolve(name);
        try {
            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Логирование ошибки и выброс пользовательского исключения
            throw new AppException("Error uploading image: " + name, e);
        }
    }

    private String generateTemporaryFileName(String imageId) {
        return "temp_"
               + System.currentTimeMillis()
               + "_"
               + UUID.randomUUID().toString()
               + "_" + imageId;
    }


    public boolean isValid(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        // Проверка на наличие файла и его расширения
        if (file.isEmpty() || originalFilename == null || !originalFilename.contains(".")) {
            return false;
        }

        // Извлечение расширения файла и проверка его допустимости
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Key.ALLOWED_EXTENSIONS.contains(extension)) {
            return false;
        }

        Path filePath = null;
        try {
            // Генерация уникального имени файла и создание пути к временному файлу
            String uniqueFilename = generateTemporaryFileName(file.getOriginalFilename());
            filePath = tempFilesDir.resolve(uniqueFilename);

            // Копирование данных из MultipartFile в файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Проверка MIME-типа временного файла
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
            // Логирование ошибки с уровнем WARN.
            return false;
        }
    }


    private boolean isPathSecure(String imageId) {
        return imageId != null && !imageId.contains("..");
    }

    private void deleteOldFiles(String imageId) {
        for (String ext : Key.ALLOWED_EXTENSIONS) {
            Path path = imagesFolder.resolve(imageId + ext);
            if (Files.exists(path)) {
                if (!tryDeleteFile(path)) {
                    // TODO log
                }
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


//    private void uploadImageInternal(String name, InputStream data) {
//        Path targetPath = imagesFolder.resolve(name);
//        try {
//            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            // выкинуть исключение пользовательское?
//            // logger.error("Error uploading image: " + name, e);
//        }
//    }

    public void deleteExpiredTempFiles() {
        File folder = tempFilesDir.toFile();
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            // Можно добавить логирование об ошибке доступа к каталогу
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
                        // Логирование ошибки при парсинге времени
                    }
                }
            }
        }
    }


    @Scheduled(cron = "0 */10 * * * ?")  // 10 min
    public void scheduledDeleteExpiredFiles() {
        deleteExpiredTempFiles();
    }


}
