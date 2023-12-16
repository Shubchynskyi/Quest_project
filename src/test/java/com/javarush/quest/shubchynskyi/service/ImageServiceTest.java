package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.javarush.quest.shubchynskyi.constant.Key.INVALID_FILE_TYPE;
import static com.javarush.quest.shubchynskyi.constant.Key.NO_IMAGE_JPG;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;
    private MockedStatic<Files> mockedFiles;
    @Mock
    private MultipartFile file;
    private static final String imagesDirectory = "target/test-images";

    @BeforeEach
    public void setUp() throws IOException {
        imageService = new ImageService(imagesDirectory);

        mockedFiles = Mockito.mockStatic(Files.class);
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("image/jpeg");
    }

    @AfterEach
    public void tearDown() {
        mockedFiles.close();
    }


    // Тестирование создания директорий при инициализации сервиса
    @Test
    public void shouldCreateDirectoriesOnInitialization() throws IOException {
        // Вызов конструктора для инициализации сервиса
        new ImageService(imagesDirectory);

        // Проверка, что метод createDirectories был вызван для основной директории
        mockedFiles.verify(() -> Files.createDirectories(Paths.get(imagesDirectory)), times(1));

        // Проверка, что метод createDirectories был вызван для директории временных файлов
        Path tempFilesDir = Paths.get(imagesDirectory).resolve(Key.PREFIX_FOR_TEMP_IMAGES);
        mockedFiles.verify(() -> Files.createDirectories(tempFilesDir), times(1));
    }


    // ***************************** ТЕСТИРОВАНИЕ ПУТИ ***************************** //

    // Тестирование получения пути к изображению с корректным именем файла
    @Test
    public void shouldReturnPathWhenImagePathIsValid() {
        String validFileName = "testImage.jpg";
        Path expectedPath = Paths.get(imagesDirectory).resolve(validFileName);

        Path result = imageService.getImagePath(validFileName);

        assertEquals(expectedPath, result);
        mockedFiles.verify(() -> Files.exists(expectedPath), times(1));
    }

    // Тестирование получения пути к изображению с некорректным именем файла (параметризованный тест)
    @ParameterizedTest
    @ValueSource(strings = {"../invalidPath", "invalid*path", "invalid?path", "", " ", "invalid\0path", "\ninvalid"})
    public void shouldThrowExceptionWhenImagePathIsInvalid(String invalidePath) {
        assertThrows(SecurityException.class, () -> imageService.getImagePath(invalidePath));
    }

    @Test
    public void Should_ReturnDefaultImagePath_When_FileDoesNotExist() {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

        String filename = "nonexistent";
        Path expectedPath = Paths.get(imagesDirectory, NO_IMAGE_JPG);

        Path result = imageService.getImagePath(filename);

        assertEquals(expectedPath, result);
    }

    @Test
    public void shouldThrowExceptionWhenFileNameIsNull() {
        // Проверка, что метод бросает исключение при передаче null в качестве имени файла
        assertThrows(AppException.class, () -> imageService.getImagePath(null));
    }


// ***************************** Тесты, связанные с загрузкой файла через MultipartFile ***************************** //

    // Тестирование загрузки файла через MultipartFile с корректными данными
    @Test
    public void shouldUploadFileFromMultipartFile() {
        // Настройка мока MultipartFile
        String originalFilename = "testImage.jpg";
        String imageId = "uniqueImageId";
        boolean isTemporary = false;
        byte[] fileContent = "fileContent".getBytes();
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", originalFilename, MediaType.IMAGE_JPEG_VALUE, fileContent);

        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenReturn(1L);
        mockedFiles.when(() -> Files.deleteIfExists(any(Path.class)))
                .thenReturn(true);

        String resultFilename = imageService.uploadFromMultipartFile(mockMultipartFile, imageId, isTemporary);

        Assertions.assertNotNull(resultFilename);
        Assertions.assertTrue(resultFilename.contains(imageId));

        Path expectedPath = Paths.get(imagesDirectory).resolve(imageId + ".jpg");
        mockedFiles.verify(() -> Files.copy(
                any(InputStream.class),
                eq(expectedPath),
                eq(StandardCopyOption.REPLACE_EXISTING)
        ), times(1));
    }

    // Тестирование Обработки Исключений при Загрузке
    @Test
    public void shouldHandleIOExceptionOnFileUpload() {
        // Настройка мока MultipartFile
        String originalFilename = "testImage.jpg";
        String imageId = "uniqueImageId";
        boolean isTemporary = false;
        byte[] fileContent = "fileContent".getBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", originalFilename, MediaType.IMAGE_JPEG_VALUE, fileContent);

        // Имитация IOException при попытке сохранить файл
        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenThrow(new IOException());

        // Ожидаемое исключение
        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockMultipartFile, imageId, isTemporary)
        );

        // Проверка сообщения исключения
        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    // Ошибки Записи Файла
    @Test
    public void shouldHandleZeroBytesWrittenOnMultipartFileUpload() {
        String originalFilename = "testImage.jpeg";
        String imageId = "uniqueImageId";
        boolean isTemporary = false;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", originalFilename, MediaType.IMAGE_JPEG_VALUE, new byte[10]);

        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenReturn(0L);
        mockedFiles.when(() -> Files.probeContentType(any(Path.class)))
                .thenReturn("application/octet-stream");

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockMultipartFile, imageId, isTemporary)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    // Проверка MIME-типа
    @Test
    public void shouldRejectInvalidMimeTypeOnMultipartFileUpload() {
        String originalFilename = "testImage.jpg";
        String imageId = "uniqueImageId";
        boolean isTemporary = false;
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", originalFilename, MediaType.IMAGE_JPEG_VALUE, new byte[10]);

        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("application/octet-stream");

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockMultipartFile, imageId, isTemporary)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    // Тестирование обработки пустого MultipartFile
    @Test
    public void shouldHandleEmptyMultipartFile() {
        // Создание пустого мока MultipartFile
        MockMultipartFile emptyMultipartFile =
                new MockMultipartFile("file", "", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        // Вызов метода uploadFromMultipartFile с пустым файлом
        String resultFilename = imageService.uploadFromMultipartFile(emptyMultipartFile, "uniqueImageId", false);

        // Проверка, что метод корректно обработал пустой файл
        // Ожидаемое поведение - возврат null
        Assertions.assertNull(resultFilename);
    }

    @Test
    public void Should_UploadImage_When_GivenValidFileAndImageId() {
        String imageId = "testId";
        String originalFilename = "test.jpeg";
        String mimeType = "image/jpeg";
        byte[] content = "fakeImageContent".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile("file", originalFilename, mimeType, content);

        imageService.uploadFromMultipartFile(mockFile, imageId, false);

        Path uploadedImagePath = Paths.get(imagesDirectory, imageId + ".jpeg");
        Assertions.assertTrue(Files.exists(uploadedImagePath));
    }

    @Test
    public void Should_ThrowAppException_When_MimeTypeIsInvalid() {
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.isEmpty()).thenReturn(false);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(file, "testId", false));

        Assertions.assertTrue(exception.getMessage().contains(INVALID_FILE_TYPE));
    }

    // Тестирование обработки недопустимого файла при загрузке
    @Test
    public void shouldThrowExceptionForInvalidFileOnUpload() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, new byte[10]);

        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("text/plain");

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(invalidFile, "uniqueImageId", false)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    // Тестирование валидации файла по MultipartFile
    @Test
    public void shouldValidateMultipartFile() {
        MockMultipartFile validFile = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[10]);

        Assertions.assertTrue(imageService.isValid(validFile));
    }


// ***************************** Тесты, связанные с безопасностью пути ***************************** //

    // Тестирование обработки небезопасного пути при загрузке файла
    @ParameterizedTest
    @ValueSource(strings = {
            "../invalidPath",
            "invalid*path",
            "invalid?path",
            " ",
            "invalid\0path",
            "\ninvalid",
            "..",
            "./",
            "/../",
            "//",
            "C:\\",
            "/etc/passwd",
            "invalidName|"
    })
    public void shouldThrowExceptionWhenPathIsInsecureForMultipartFile(String insecurePath) {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", "testImage.jpg", MediaType.IMAGE_JPEG_VALUE, "testContent".getBytes());

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockMultipartFile, insecurePath, false)
        );

        // Проверка, что исключение содержит ожидаемое сообщение
        String expectedMessage = Key.THE_FILE_PATH_IS_INSECURE;
        Assertions.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void Should_ThrowException_When_FilePathIsInsecure() {
        String insecureImageId = "../testId";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", "fakeImageContent".getBytes());

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, insecureImageId, false));
    }


    // ***************************** Тесты, связанные с обработкой существующих файлов ***************************** //

    @Test
    public void shouldUploadExistingFile() throws IOException {
        String existingFileName = "existingFile.jpg";
        String imageId = "testImageId";

        mockedFiles.when(() -> Files.isReadable(any(Path.class)))
                .thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class)))
                .thenReturn(new ByteArrayInputStream(new byte[0]));

        // Вызов метода uploadFromExistingFile
        imageService.uploadFromExistingFile(existingFileName, imageId);

        // Проверка, что методы Files вызывались с правильными аргументами
        Path expectedFilePath = Paths.get(imagesDirectory).resolve(existingFileName);
        mockedFiles.verify(() -> Files.exists(expectedFilePath), times(3));
        mockedFiles.verify(() -> Files.newInputStream(expectedFilePath), times(1));
    }

    // Тестирование Поведения После Успешной Валидации
    @Test
    public void shouldSaveValidExistingFile() {
        String existingFileName = "existingFile.jpg";
        String imageId = "testImageId";

        mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenReturn(new ByteArrayInputStream(new byte[0]));

        // Вызов метода uploadFromExistingFile
        imageService.uploadFromExistingFile(existingFileName, imageId);

        // Проверка, что файл был сохранен
        Path expectedFilePath = Paths.get(imagesDirectory).resolve(imageId + ".jpg");
        mockedFiles.verify(() -> Files.copy(
                any(InputStream.class),
                eq(expectedFilePath),
                eq(StandardCopyOption.REPLACE_EXISTING)
        ), times(1));
    }

    // Проверка Сохранения Файла
    @Test
    public void shouldSaveValidExistingFileAfterValidation() throws IOException {
        String existingFileName = "existingFile.jpg";
        String imageId = "testImageId";

        mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenReturn(new ByteArrayInputStream(new byte[0]));

        imageService.uploadFromExistingFile(existingFileName, imageId);

        Path expectedPath = Paths.get(imagesDirectory).resolve(imageId + ".jpg");
        mockedFiles.verify(() -> Files.copy(
                any(InputStream.class),
                eq(expectedPath),
                eq(StandardCopyOption.REPLACE_EXISTING)
        ), times(1));
    }

    // Обработка Ошибок Чтения Файла
    @Test
    public void shouldHandleIOExceptionOnExistingFileUpload() throws IOException {
        String existingFileName = "existingFile.jpg";
        String imageId = "testImageId";

        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenThrow(new IOException());

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(existingFileName, imageId)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    // Валидация Конечного Файла
    @Test
    public void shouldValidateFinalFileOnExistingFileUpload() {
        String invalidFileName = "invalidFile.mp3";
        String imageId = "testImageId";

        mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("audio/mpeg");

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(invalidFileName, imageId)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_ThrowException_When_FileDoesNotExist() {
        String imageId = "secureImageId";
        String nonExistingFileName = "nonExisting.png";
        Path filePath = Paths.get(nonExistingFileName);

        mockedFiles.when(() -> Files.exists(filePath)).thenReturn(false);

        // Проверка, что метод выбросит исключение AppException, если файл не существует
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(nonExistingFileName, imageId),
                "Expected AppException to be thrown if the file does not exist"
        );
    }


// ***************************** Тесты, связанные с обработкой идентификаторов изображений ***************************** //

    @Test
    public void Should_ThrowAppException_When_ImageIdIsEmpty() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(file, "", false));
    }

    @Test
    public void Should_ThrowAppException_When_ImageIdIsNull() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(file, null, false));
    }

    @Test
    public void Should_ThrowException_When_FileExtensionIsNotAllowed() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.exe", "application/octet-stream", new byte[100]);

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, "testId", false));
    }

    @Test
    public void Should_DeleteOldFiles_When_UploadNewFileWithSameImageId() throws IOException {

        mockedFiles.when(() -> Files.readAllBytes(any(Path.class)))
                .thenReturn("fakeImageContent1".getBytes(), "fakeImageContent2".getBytes()); // Возвращаем содержимое файла


        String imageId = "testId";
        Path uploadedImagePath = Paths.get(imagesDirectory, imageId + ".jpeg");

        String originalFilename1 = "test1.jpeg";
        String mimeType1 = "image/jpeg";
        byte[] content1 = "fakeImageContent1".getBytes();
        MockMultipartFile mockFile1 = new MockMultipartFile("file", originalFilename1, mimeType1, content1);

        String originalFilename2 = "test2.jpeg";
        String mimeType2 = "image/jpeg";
        byte[] content2 = "fakeImageContent2".getBytes();
        MockMultipartFile mockFile2 = new MockMultipartFile("file", originalFilename2, mimeType2, content2);

        imageService.uploadFromMultipartFile(mockFile1, imageId, false);
        byte[] fileBytes1 = Files.readAllBytes(uploadedImagePath);
        assertArrayEquals(content1, fileBytes1);

        imageService.uploadFromMultipartFile(mockFile2, imageId, false);
        byte[] fileBytes2 = Files.readAllBytes(uploadedImagePath);
        assertArrayEquals(content2, fileBytes2);

        assertNotEquals(fileBytes1, fileBytes2);
    }


    // ********************* Тестирование запланированного удаления временных файлов *********************
    @Test
    public void shouldScheduleDeletionOfExpiredFiles() throws IOException {
        // Создание мока сервиса
        ImageService mockImageService = Mockito.spy(new ImageService(imagesDirectory));

        // Вызов запланированного метода
        mockImageService.scheduledDeleteExpiredFiles();

        // Проверка, что метод deleteExpiredTempFiles был вызван
        Mockito.verify(mockImageService, times(1)).deleteExpiredTempFiles();
    }


}
