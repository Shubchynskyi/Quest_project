package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.exception.AppException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.javarush.quest.shubchynskyi.constant.Key.INVALID_FILE_TYPE;
import static com.javarush.quest.shubchynskyi.constant.Key.NO_IMAGE_JPG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;

    @Mock
    private MultipartFile file;

    private static final String imagesDirectory = "target/test-images";

    @BeforeEach
    public void setUp() throws IOException {
        imageService = new ImageService(imagesDirectory);
    }

    @Test
    public void Should_UploadImage_When_GivenValidFileAndImageId() {
        String imageId = "testId";
        String originalFilename = "test.png";
        String mimeType = "image/png";
        byte[] content = "fakeImageContent".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile("file", originalFilename, mimeType, content);

        imageService.uploadFromMultipartFile(mockFile, imageId, false);

        Path uploadedImagePath = Paths.get(imagesDirectory, imageId + ".png");
        assertTrue(Files.exists(uploadedImagePath));
    }

    @Test
    public void Should_ReturnCorrectImagePath_When_FileExists() {
        String filename = "no-image";
        Path expectedPath = Paths.get(imagesDirectory, "no-image.jpg");

        Path result = imageService.getImagePath(filename);

        assertEquals(expectedPath, result);
    }

    @Test
    public void Should_ThrowAppException_When_MimeTypeIsInvalid() {
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.isEmpty()).thenReturn(false);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(file, "testId", false));

        assertTrue(exception.getMessage().contains(INVALID_FILE_TYPE));
    }

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
    public void Should_ReturnDefaultImagePath_When_FileDoesNotExist() {
        String filename = "nonexistent";
        Path expectedPath = Paths.get(imagesDirectory, NO_IMAGE_JPG);

        Path result = imageService.getImagePath(filename);

        assertEquals(expectedPath, result);
    }

    @Test
    public void Should_DeleteOldFiles_When_UploadNewFileWithSameImageId() throws IOException {
        String imageId = "testId";
        Path uploadedImagePath = Paths.get(imagesDirectory, imageId + ".png");

        String originalFilename1 = "test1.png";
        String mimeType1 = "image/png";
        byte[] content1 = "fakeImageContent1".getBytes();
        MockMultipartFile mockFile1 = new MockMultipartFile("file", originalFilename1, mimeType1, content1);

        String originalFilename2 = "test2.png";
        String mimeType2 = "image/png";
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

    @Test
    public void Should_ThrowException_When_FilePathIsInsecure() {
        String insecureImageId = "../testId";
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", "fakeImageContent".getBytes());

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, insecureImageId, false));
    }

    @Test
    public void Should_ThrowException_When_FileExtensionIsNotAllowed() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.exe", "application/octet-stream", new byte[0]);

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, "testId", false));
    }

    @Test
    public void Should_CorrectlyHandleEmptyAndWhitespaceImageId_When_Uploading() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, " ", false));

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, "", false));
    }

    @Test
    public void Should_ReturnDefaultImagePath_When_ImageWithMultipleExtensionsNotFound() {
        String filename = "multiextension";
        Path expectedPath = Paths.get(imagesDirectory, NO_IMAGE_JPG);

        Path result = imageService.getImagePath(filename);

        assertEquals(expectedPath, result);
    }

    @Test
    public void should_ThrowException_When_FileDoesNotExist() throws IOException {
        String imageId = "secureImageId";
        String nonExistingFileName = "nonExisting.png";
        Path filePath = Paths.get(nonExistingFileName);

        // Мокирование статического метода Files.exists
        Mockito.mockStatic(Files.class).when(() -> Files.exists(filePath)).thenReturn(false);

        // Проверка, что метод выбросит исключение AppException, если файл не существует
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(nonExistingFileName, imageId),
                "Expected AppException to be thrown if the file does not exist"
        );
    }




}
