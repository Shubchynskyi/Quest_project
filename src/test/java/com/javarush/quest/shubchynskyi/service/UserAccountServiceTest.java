package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.result.UserDataProcessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private UserAccountService userAccountService;

    private UserDTO userDTOFromModel;
    private User user;
    private MultipartFile imageFile;
    private String tempImageId;
    private String originalLogin;
    private BindingResult bindingResult;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        userDTOFromModel = new UserDTO();
        userDTOFromModel.setLogin("testLogin");
        user = new User();
        imageFile = mock(MultipartFile.class);
        tempImageId = "tempImageId";
        originalLogin = "";
        bindingResult = mock(BindingResult.class);
        redirectAttributes = mock(RedirectAttributes.class);
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false",
            "true, false",
            "false, true"
    })
    void registerNewUser_ShouldCallAppropriateServices(boolean isTempImagePresent, boolean isImageValid) {
        when(userMapper.userDTOToUser(userDTOFromModel)).thenReturn(user);
        when(userService.create(user)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDTOWithoutPassword(user)).thenReturn(new UserDTO());

        userAccountService.registerNewUser(userDTOFromModel, imageFile, tempImageId, isTempImagePresent, isImageValid);

        verify(userMapper).userDTOToUser(userDTOFromModel);
        verify(userService).create(user);
        if (isTempImagePresent && !isImageValid) {
            verify(imageService).uploadFromExistingFile(eq(tempImageId), anyString());
        } else {
            verify(imageService).uploadFromMultipartFile(imageFile, user.getImage(), false);
        }
        verify(userMapper).userToUserDTOWithoutPassword(user);
    }

    @Test
    void deleteExistingUser_ShouldCallAppropriateServices() {
        userAccountService.deleteExistingUser(user);

        verify(userService).delete(user);
        verify(imageService).deleteOldFiles(user.getImage());
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false",
            "true, false",
            "false, true"
    })
    void updateExistingUser_ShouldCallAppropriateServices(boolean isTempImagePresent, boolean isImageValid) {
        userAccountService.updateExistingUser(user, imageFile, tempImageId, isTempImagePresent, isImageValid);

        verify(userService).update(user);
        if (isTempImagePresent && !isImageValid) {
            verify(imageService).uploadFromExistingFile(eq(tempImageId), anyString());
        } else {
            verify(imageService).uploadFromMultipartFile(imageFile, user.getImage(), false);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "true, true, true, true, true",
            "true, true, true, false, false",
            "true, true, false, true, false",
            "true, true, false, false, false",
            "true, false, true, true, false",
            "true, false, true, false, false",
            "true, false, false, true, false",
            "true, false, false, false, false",
            "false, true, true, true, false",
            "false, true, true, false, true",
            "false, true, false, true, false",
            "false, true, false, false, false",
            "false, false, true, true, false",
            "false, false, true, false, false",
            "false, false, false, true, false",
            "false, false, false, false, false"
    })
    void processUserData_ShouldValidateAndProcessData(boolean hasFieldsErrors, boolean isImageValid, boolean isTempImagePresent, boolean loginExists, boolean fileSizeExceedsLimit) {
        when(bindingResult.hasErrors()).thenReturn(hasFieldsErrors);
        when(imageService.isValid(imageFile)).thenReturn(isImageValid);
        when(userService.isLoginExist(anyString())).thenReturn(loginExists);
        long fileSize = fileSizeExceedsLimit ? MAX_FILE_SIZE + 1 : MAX_FILE_SIZE - 1;
        lenient().when(imageFile.getSize()).thenReturn(fileSize);

        UserDataProcessResult result = userAccountService.processUserData(
                userDTOFromModel, bindingResult, imageFile, tempImageId,
                redirectAttributes, originalLogin);

        if (isImageValid && imageFile.getSize() > MAX_FILE_SIZE) {
            isImageValid = false;
            verify(redirectAttributes).addFlashAttribute(eq(IMAGING_ERROR), anyString());
            assertFalse(result.imageIsValid());
            assertTrue(result.hasFieldsErrors());
            assertEquals("", result.tempImageId());
        }

        assertNotNull(result);

        if (hasFieldsErrors) {
            verify(validationService).processFieldErrors(bindingResult, redirectAttributes);
        }

        verify(imageService).isValid(imageFile);
        if (!isImageValid && !imageFile.isEmpty()) {
            verify(redirectAttributes).addFlashAttribute(eq(IMAGING_ERROR), anyString());
        }

        if (isTempImagePresent && !imageFile.isEmpty()) {
            verify(imageFile, atLeastOnce()).isEmpty();
        }

        if (!originalLogin.isEmpty() && !userDTOFromModel.getLogin().equals(originalLogin)) {
            verify(userService).isLoginExist(anyString());
            if (loginExists) {
                verify(redirectAttributes).addFlashAttribute(eq(ERROR), anyString());
            }
        }

        if (!hasFieldsErrors && isImageValid && !fileSizeExceedsLimit) {
            verify(validationService, never()).processFieldErrors(any(BindingResult.class), any(RedirectAttributes.class));
            verify(redirectAttributes, never()).addFlashAttribute(eq(IMAGING_ERROR), anyString());
        }

        if (hasFieldsErrors && isImageValid && !loginExists) {
            verify(imageService).uploadFromMultipartFile(eq(imageFile), anyString(), eq(true));
            verify(redirectAttributes).addFlashAttribute(eq(TEMP_IMAGE_ID), any());
        }

        if (hasFieldsErrors && isTempImagePresent && !loginExists) {
            verify(redirectAttributes).addFlashAttribute(eq(TEMP_IMAGE_ID), any());
        }

        hasFieldsErrors = hasFieldsErrors || loginExists || !isImageValid;

        assertEquals(hasFieldsErrors, result.hasFieldsErrors());
        assertEquals(isImageValid, result.imageIsValid());
    }

}

