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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.javarush.quest.shubchynskyi.constant.Key.INVALID_FILE_TYPE;
import static com.javarush.quest.shubchynskyi.constant.Key.NO_IMAGE_JPG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;
    private MockedStatic<Files> mockedFiles;
    private static final String TEST_IMAGES_DIRECTORY = "target/test-images";
    private static final String TEST_IMAGE_ID = "testImageId";
    private static final String VALID_FILE_NAME = TEST_IMAGE_ID + ".jpeg";
    private static final String INVALID_FILE_NAME = TEST_IMAGE_ID + ".txt";
    private static final String MULTIPART_FILE_NAME = "file";
    private static final String GENERAL_CONTENT_TYPE = "application/octet-stream";
    public static final String EXPECTED_APP_EXCEPTION_TO_BE_THROWN_IF_THE_FILE_DOES_NOT_EXIST = "Expected AppException to be thrown if the file does not exist";
    private static final Path uploadedImagePath = Paths.get(TEST_IMAGES_DIRECTORY, VALID_FILE_NAME);
    private static final Path expectedFilePath = Paths.get(TEST_IMAGES_DIRECTORY).resolve(VALID_FILE_NAME);
    private static final MockMultipartFile MOCK_MULTIPART_FILE =
            new MockMultipartFile(MULTIPART_FILE_NAME, VALID_FILE_NAME, MediaType.IMAGE_JPEG_VALUE, new byte[10]);

    @BeforeEach
    public void setUp() throws IOException {
        imageService = new ImageService(TEST_IMAGES_DIRECTORY);

        mockedFiles = Mockito.mockStatic(Files.class);
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn(MediaType.IMAGE_JPEG_VALUE);
    }

    @AfterEach
    public void tearDown() {
        mockedFiles.close();
    }

    @Test
    public void should_CreateDirectories_When_Initialization() throws IOException {
        new ImageService(TEST_IMAGES_DIRECTORY);

        mockedFiles.verify(() -> Files.createDirectories(Paths.get(TEST_IMAGES_DIRECTORY)), times(1));

        Path tempFilesDir = Paths.get(TEST_IMAGES_DIRECTORY).resolve(Key.PREFIX_FOR_TEMP_IMAGES);
        mockedFiles.verify(() -> Files.createDirectories(tempFilesDir), times(1));
    }

    @Test
    public void should_ReturnPath_When_ImagePathIsValid() {
        Path result = imageService.getImagePath(VALID_FILE_NAME);

        assertEquals(expectedFilePath, result);
        mockedFiles.verify(() -> Files.exists(expectedFilePath), times(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../invalidPath", "invalid*path", "invalid?path", "", " ", "invalid\0path", "\ninvalid"})
    public void should_ThrowException_When_ImagePathIsInvalid(String invalidePath) {
        assertThrows(SecurityException.class, () -> imageService.getImagePath(invalidePath));
    }

    @Test
    public void should_ReturnDefaultImagePath_When_FileDoesNotExist() {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

        Path expectedPath = Paths.get(TEST_IMAGES_DIRECTORY, NO_IMAGE_JPG);
        Path result = imageService.getImagePath(VALID_FILE_NAME);

        assertEquals(expectedPath, result);
    }

    @Test
    public void should_ThrowException_When_FileNameIsNull() {
        assertThrows(AppException.class, () -> imageService.getImagePath(null));
    }

    @Test
    public void should_UploadFile_When_GivenMultipartFile() {
        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenReturn(1L);
        mockedFiles.when(() -> Files.deleteIfExists(any(Path.class)))
                .thenReturn(true);

        String resultFilename = imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, TEST_IMAGE_ID, false);

        Assertions.assertNotNull(resultFilename);
        Assertions.assertTrue(resultFilename.contains(TEST_IMAGE_ID));

        mockedFiles.verify(() -> Files.copy(
                any(InputStream.class),
                eq(expectedFilePath),
                eq(StandardCopyOption.REPLACE_EXISTING)
        ), times(1));
    }

    @Test
    public void should_HandleIOException_When_FileUploadFails() {
        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenThrow(new IOException());

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, TEST_IMAGE_ID, false)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_HandleZeroBytesWritten_When_MultipartFileUpload() {
        mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                .thenReturn(0L);
        mockedFiles.when(() -> Files.probeContentType(any(Path.class)))
                .thenReturn(GENERAL_CONTENT_TYPE);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, TEST_IMAGE_ID, false)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_RejectInvalidMimeType_When_MultipartFileUpload() {
        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn(GENERAL_CONTENT_TYPE);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, TEST_IMAGE_ID, false)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_HandleEmpty_When_MultipartFileUpload() {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile(MULTIPART_FILE_NAME, "", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        String resultFilename = imageService.uploadFromMultipartFile(emptyMultipartFile, TEST_IMAGE_ID, false);

        Assertions.assertNull(resultFilename);
    }

    @Test
    public void should_UploadImage_When_GivenValidFileAndImageId() {
        imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, TEST_IMAGE_ID, false);

        Assertions.assertTrue(Files.exists(uploadedImagePath));
    }

    @Test
    public void should_ThrowException_When_InvalidFileUploaded() {
        MockMultipartFile invalidFile = new MockMultipartFile(MULTIPART_FILE_NAME, INVALID_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, new byte[10]);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(invalidFile, TEST_IMAGE_ID, false)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_Validate_When_MultipartFileGiven() {
        Assertions.assertTrue(imageService.isValid(MOCK_MULTIPART_FILE));
    }

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
    public void should_ThrowException_When_PathIsInsecureForMultipartFile(String insecurePath) {
        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, insecurePath, false)
        );

        String expectedMessage = Key.THE_FILE_PATH_IS_INSECURE;
        Assertions.assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void should_ThrowException_When_FilePathIsInsecure() {
        String insecureImageId = "../testId";

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, insecureImageId, false));
    }

    @Test
    public void should_UploadFile_When_ExistingFileGiven() {
        mockedFiles.when(() -> Files.isReadable(any(Path.class)))
                .thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class)))
                .thenReturn(new ByteArrayInputStream(new byte[0]));

        imageService.uploadFromExistingFile(VALID_FILE_NAME, TEST_IMAGE_ID);

        mockedFiles.verify(() -> Files.exists(expectedFilePath), times(3));
        mockedFiles.verify(() -> Files.newInputStream(expectedFilePath), times(1));
    }

    @Test
    public void should_SaveFile_When_ValidExistingFileGiven() {
        mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenReturn(new ByteArrayInputStream(new byte[0]));

        imageService.uploadFromExistingFile(VALID_FILE_NAME, TEST_IMAGE_ID);

        mockedFiles.verify(() -> Files.copy(
                any(InputStream.class),
                eq(expectedFilePath),
                eq(StandardCopyOption.REPLACE_EXISTING)
        ), times(1));
    }

    @Test
    public void should_HandleIOException_When_ExistingFileUpload() {
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenThrow(new IOException());

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(VALID_FILE_NAME, TEST_IMAGE_ID)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_ValidateFinalFile_When_ExistingFileUploaded() {
        mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
        mockedFiles.when(() -> Files.newInputStream(any(Path.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
        mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        AppException exception = assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(VALID_FILE_NAME, TEST_IMAGE_ID)
        );

        assertEquals(INVALID_FILE_TYPE, exception.getMessage());
    }

    @Test
    public void should_ThrowException_When_FileDoesNotExist() {
        mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromExistingFile(VALID_FILE_NAME, TEST_IMAGE_ID),
                EXPECTED_APP_EXCEPTION_TO_BE_THROWN_IF_THE_FILE_DOES_NOT_EXIST
        );
    }

    @Test
    public void should_ThrowAppException_When_ImageIdIsEmpty() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, "", false));
    }

    @Test
    public void should_ThrowAppException_When_ImageIdIsNull() {
        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(MOCK_MULTIPART_FILE, null, false));
    }

    @Test
    public void should_ThrowException_When_FileExtensionIsNotAllowed() {
        MockMultipartFile mockFile = new MockMultipartFile(MULTIPART_FILE_NAME, INVALID_FILE_NAME, GENERAL_CONTENT_TYPE, new byte[10]);

        assertThrows(
                AppException.class,
                () -> imageService.uploadFromMultipartFile(mockFile, TEST_IMAGE_ID, false));
    }

    @Test
    public void should_DeleteOldFiles_When_UploadNewFileWithSameImageId() throws IOException {
        mockedFiles.when(() -> Files.readAllBytes(any(Path.class)))
                .thenReturn("fakeImageContent1".getBytes())
                .thenReturn("fakeImageContent2".getBytes());

        String originalFilename1 = "test1.jpeg";
        byte[] content1 = "fakeImageContent1".getBytes();
        MockMultipartFile mockFile1 = new MockMultipartFile(MULTIPART_FILE_NAME, originalFilename1, MediaType.IMAGE_JPEG_VALUE, content1);

        String originalFilename2 = "test2.jpeg";
        byte[] content2 = "fakeImageContent2".getBytes();
        MockMultipartFile mockFile2 = new MockMultipartFile(MULTIPART_FILE_NAME, originalFilename2, MediaType.IMAGE_JPEG_VALUE, content2);

        imageService.uploadFromMultipartFile(mockFile1, TEST_IMAGE_ID, false);
        byte[] fileBytes1 = Files.readAllBytes(uploadedImagePath);
        Assertions.assertArrayEquals(content1, fileBytes1);

        imageService.uploadFromMultipartFile(mockFile2, TEST_IMAGE_ID, false);
        byte[] fileBytes2 = Files.readAllBytes(uploadedImagePath);
        Assertions.assertArrayEquals(content2, fileBytes2);

        Assertions.assertNotEquals(fileBytes1, fileBytes2);
    }

    @Test
    public void should_ScheduleDeletion_When_FilesExpired() throws IOException {
        ImageService mockImageService = Mockito.spy(new ImageService(TEST_IMAGES_DIRECTORY));

        mockImageService.scheduledDeleteExpiredFiles();

        Mockito.verify(mockImageService, times(1)).deleteExpiredTempFiles();
    }
}
