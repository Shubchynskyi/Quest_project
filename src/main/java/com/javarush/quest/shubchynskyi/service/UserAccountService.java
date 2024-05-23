package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.result.UserDataProcessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;


@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    @Transactional
    public UserDTO registerNewUser(UserDTO userDTOFromModel,
                                   MultipartFile imageFile,
                                   String tempImageId,
                                   boolean isTempImagePresent,
                                   boolean imageIsValid) {
        User user = userMapper.userDTOToUser(userDTOFromModel);
        User createdUser = userService.create(user).orElseThrow();

        if (!isTempImagePresent || imageIsValid) {
            imageService.uploadFromMultipartFile(imageFile, createdUser.getImage(), false);
        } else {
            imageService.uploadFromExistingFile(tempImageId, createdUser.getImage());
        }

        return userMapper.userToUserDTOWithoutPassword(createdUser);
    }

    @Transactional
    public void deleteExistingUser(User user) {
        userService.delete(user);
        imageService.deleteOldFiles(user.getImage());
    }

    @Transactional
    public void updateExistingUser(User userFromModel,
                                   MultipartFile imageFile,
                                   String tempImageId,
                                   boolean isTempImagePresent,
                                   boolean imageIsValid
    ) {
        userService.update(userFromModel);

        if (!isTempImagePresent || imageIsValid) {
            imageService.uploadFromMultipartFile(imageFile, userFromModel.getImage(), false);
        } else {
            imageService.uploadFromExistingFile(tempImageId, userFromModel.getImage());
        }
    }

    public UserDataProcessResult processUserData(
            UserDTO userDTOFromModel,
            BindingResult bindingResult,
            MultipartFile imageFile,
            String tempImageId,
            RedirectAttributes redirectAttributes,
            String originalLogin) {
        boolean hasFieldsErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);
        boolean imageIsValid = imageService.isValid(imageFile);
        boolean isTempImagePresent = !tempImageId.isEmpty();

        if (imageIsValid && imageFile.getSize() > MAX_FILE_SIZE) {
            addLocalizedMaxSizeError(redirectAttributes);
            hasFieldsErrors = true;
            imageIsValid = false;
            if (!tempImageId.isEmpty()) {
                tempImageId = EMPTY_STRING;
            }
        } else if ((!imageIsValid && !imageFile.isEmpty())) {
            addLocalizedIncorrectImageError(redirectAttributes);
            hasFieldsErrors = true;
        }

        if (originalLogin.isEmpty() || !userDTOFromModel.getLogin().equals(originalLogin)) {
            hasFieldsErrors = isLoginExist(userDTOFromModel, redirectAttributes, hasFieldsErrors);
        }

        if (hasFieldsErrors && imageIsValid) {
            String tempImageUUID = UUID.randomUUID().toString();
            String fullTempImageName = imageService.uploadFromMultipartFile(imageFile, tempImageUUID, true);
            redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, fullTempImageName);
        } else if (hasFieldsErrors && isTempImagePresent) {
            redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, tempImageId);
        }

        return new UserDataProcessResult(tempImageId, hasFieldsErrors, isTempImagePresent, imageIsValid);
    }

    private void addLocalizedIncorrectImageError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(IMAGE_FILE_IS_INCORRECT);
        redirectAttributes.addFlashAttribute(IMAGING_ERROR, localizedMessage);
    }

    private void addLocalizedMaxSizeError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(FILE_IS_TOO_LARGE);
        redirectAttributes.addFlashAttribute(
                IMAGING_ERROR, localizedMessage
                        + " " + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                        + " " + MB);
    }

    //TODO refactoring - need to call validationService.processFieldErrors and modify to return boolean
//    private boolean processFieldErrors(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
//        boolean hasFieldsErrors = bindingResult.hasErrors();
//        if (hasFieldsErrors) {
//            validationService.processFieldErrors(bindingResult, redirectAttributes);
//        }
//        return hasFieldsErrors;
//    }

    private boolean isLoginExist(UserDTO userDTOFromModel, RedirectAttributes redirectAttributes, boolean hasErrors) {
        if (userService.isLoginExist(userDTOFromModel.getLogin())) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            hasErrors = true;
        }
        return hasErrors;
    }

}