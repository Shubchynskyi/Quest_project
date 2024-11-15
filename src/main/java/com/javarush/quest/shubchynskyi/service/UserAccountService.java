package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.config.ImageProperties;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.result.UserDataProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserService userService;
    private final ImageService imageService;
    private final ImageProperties imageProperties;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    @Transactional
    public UserDTO registerNewUser(UserDTO userDTOFromModel,
                                   MultipartFile imageFile,
                                   String tempImageId,
                                   boolean isTempImagePresent,
                                   boolean imageIsValid) {
        log.info("Registering new user with login: {}", userDTOFromModel.getLogin());
        User user = userMapper.userDTOToUser(userDTOFromModel);
        User createdUser = userService.create(user).orElseThrow();

        if (!isTempImagePresent || imageIsValid) {
            log.info("Uploading new image file for user ID: {}", createdUser.getId());
            imageService.uploadFromMultipartFile(imageFile, createdUser.getImage(), false);
        } else {
            log.info("Uploading previously saved temporary image for user ID: {}", createdUser.getId());
            imageService.uploadFromExistingFile(tempImageId, createdUser.getImage());
        }

        return userMapper.userToUserDTOWithoutPassword(createdUser);
    }

    @Transactional
    public void deleteExistingUser(User user) {
        log.info("Deleting user with ID: {}", user.getId());
        userService.delete(user);
        imageService.deleteOldFiles(user.getImage());
    }

    @Transactional
    public Optional<User> updateExistingUser(User userFromModel,
                                   MultipartFile imageFile,
                                   String tempImageId,
                                   boolean isTempImagePresent,
                                   boolean imageIsValid) {
        log.info("Updating user with ID: {}", userFromModel.getId());
        Optional<User> updatedUser = userService.update(userFromModel);

        if (!isTempImagePresent || imageIsValid) {
            log.info("Uploading updated image for user ID: {}", userFromModel.getId());
            imageService.uploadFromMultipartFile(imageFile, userFromModel.getImage(), false);
        } else {
            log.info("Using existing temporary image for user ID: {}", userFromModel.getId());
            imageService.uploadFromExistingFile(tempImageId, userFromModel.getImage());
        }
        return updatedUser;
    }

    public UserDataProcessResult processUserData(
            UserDTO userDTOFromModel,
            BindingResult bindingResult,
            MultipartFile imageFile,
            String tempImageId,
            RedirectAttributes redirectAttributes,
            String originalLogin) {
        log.info("Processing user data for login: {}", userDTOFromModel.getLogin());
        boolean hasFieldsErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);
        boolean imageIsValid = imageService.isValid(imageFile);
        boolean isTempImagePresent = !tempImageId.isEmpty();

        if (imageIsValid && imageFile.getSize() > imageProperties.getMaxFileSize()) {
            log.warn("Image file size is too large for user: {}", userDTOFromModel.getLogin());
            addLocalizedMaxSizeError(redirectAttributes);
            hasFieldsErrors = true;
            imageIsValid = false;
            if (!tempImageId.isEmpty()) {
                tempImageId = EMPTY_STRING;
            }
        } else if (!imageIsValid && !imageFile.isEmpty()) {
            log.warn("Incorrect image file for user: {}", userDTOFromModel.getLogin());
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
                IMAGING_ERROR, localizedMessage + " " + (imageProperties.getMaxFileSize() / KB_TO_MB / KB_TO_MB) + " " + MB);
    }

    private boolean isLoginExist(UserDTO userDTOFromModel, RedirectAttributes redirectAttributes, boolean hasErrors) {
        if (userService.isLoginExist(userDTOFromModel.getLogin())) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            hasErrors = true;
        }
        return hasErrors;
    }
}