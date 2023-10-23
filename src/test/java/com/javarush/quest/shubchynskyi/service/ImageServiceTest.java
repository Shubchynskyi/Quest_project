package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.exception.AppException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private static ImageService imageService;

    @Mock
    private MultipartFile file;

    private static final String imagesDirectory = "target/test-images";

    @BeforeAll
    public static void setUp() throws IOException {
        imageService = new ImageService();
        ReflectionTestUtils.setField(imageService, "imagesDirectory", imagesDirectory);
        imageService.init();
    }

    @Test
    public void Should_UploadImage_When_GivenValidFileAndImageId() {
        String imageId = "testId";
        String originalFilename = "test.png";
        String mimeType = "image/png";
        byte[] content = "fakeImageContent".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile("file", originalFilename, mimeType, content);

        imageService.uploadImage(mockFile, imageId, false);

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
                () -> imageService.uploadImage(file, "testId", false));

        assertTrue(exception.getMessage().contains(INVALID_FILE_TYPE));
    }

    @Test
    public void Should_ThrowAppException_When_ImageIdIsEmpty() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadImage(file, "", false));
    }

    @Test
    public void Should_ThrowAppException_When_ImageIdIsNull() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadImage(file, null, false));
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

        imageService.uploadImage(mockFile1, imageId, false);
        byte[] fileBytes1 = Files.readAllBytes(uploadedImagePath);
        assertArrayEquals(content1, fileBytes1);

        imageService.uploadImage(mockFile2, imageId, false);
        byte[] fileBytes2 = Files.readAllBytes(uploadedImagePath);
        assertArrayEquals(content2, fileBytes2);

        assertNotEquals(fileBytes1, fileBytes2);
    }
}
