package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserRegistrationService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;


@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;
    private final ValidationService validationService;
    private final UserRegistrationService userRegistrationService;

    @GetMapping(SIGNUP)
    public String showSignup(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute(name = USER_DTO_FROM_MODEL) UserDTO userDTOFromModel,
                             @ModelAttribute(name = TEMP_IMAGE_ID) String tempImageId
    ) {
        if (isUserNotLoggedIn(session)) {
            prepareSignupModel(model, userDTOFromModel, tempImageId);
            return Route.SIGNUP;
        } else {
            addErrorToRedirectAttributes(redirectAttributes);
            return REDIRECT + Route.PROFILE;
        }

    }

    private void prepareSignupModel(Model model, UserDTO userDTOFromModel, String tempImageId) {
        if (userDTOFromModel != null) {
            model.addAttribute(USER_DTO_FROM_MODEL, userDTOFromModel);
        }

        if (tempImageId != null && !tempImageId.isEmpty()) {
            model.addAttribute(TEMP_IMAGE_ID, tempImageId);
        }

        model.addAttribute(ROLES, Role.values());
    }

    private void addErrorToRedirectAttributes(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(YOU_ARE_ALREADY_LOGGED_IN);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
    }

    private boolean isUserNotLoggedIn(HttpSession session) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        return currentUser == null;
    }

    @PostMapping(SIGNUP)
    public String signup(@Valid @ModelAttribute UserDTO userDTOFromModel,
                         BindingResult bindingResult,
                         @RequestParam(name = IMAGE, required = false) MultipartFile imageFile,
                         @RequestParam(name = TEMP_IMAGE_ID, required = false) String tempImageId,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes
    ) {

        try {
            boolean hasFieldsErrors = processFieldErrors(bindingResult, redirectAttributes);
            boolean imageIsValid = imageService.isValid(imageFile);
            boolean isTempImagePresent = !tempImageId.isEmpty();

            if (imageIsValid && imageFile.getSize() > MAX_FILE_SIZE) {
                addLocalizedMaxSizeError(redirectAttributes);
                hasFieldsErrors = true;
                imageIsValid = false;
                if (!tempImageId.isEmpty()) {
                    tempImageId = "";
                }
            } else if ((!imageIsValid && !imageFile.isEmpty())) {
                addLocalizedIncorrectImageError(redirectAttributes);
                hasFieldsErrors = true;
            }

            hasFieldsErrors = isLoginExist(userDTOFromModel, redirectAttributes, hasFieldsErrors);

            if (hasFieldsErrors && imageIsValid) {
                String tempImageUUID = UUID.randomUUID().toString();
                String fullTempImageName = imageService.uploadFromMultipartFile(imageFile, tempImageUUID, true);
                redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, fullTempImageName);
            } else if (hasFieldsErrors && isTempImagePresent) {
                redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, tempImageId);
            }

            if (hasFieldsErrors) {
                redirectAttributes.addFlashAttribute(USER_DTO_FROM_MODEL, userDTOFromModel);
                return REDIRECT + Route.SIGNUP;
            }

            UserDTO userDTO = userRegistrationService.registerNewUser(
                    userDTOFromModel,
                    imageFile,
                    tempImageId,
                    imageIsValid,
                    isTempImagePresent);

            request.getSession().setAttribute(USER, userDTO);
        } catch (AppException e) {
            // TODO log
            addLocalizedUnexpectedError(redirectAttributes);
            return REDIRECT + Route.SIGNUP;
        }
        return REDIRECT + Route.PROFILE;
    }

    private void addLocalizedUnexpectedError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR);
        redirectAttributes.addFlashAttribute(IMAGING_ERROR, localizedMessage);
    }

    private void addLocalizedIncorrectImageError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(IMAGE_FILE_IS_INCORRECT);
        redirectAttributes.addFlashAttribute(IMAGING_ERROR, localizedMessage);
    }

    private void addLocalizedMaxSizeError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(FILE_IS_TOO_LARGE);
        redirectAttributes.addFlashAttribute(
                IMAGING_ERROR, localizedMessage
                               + " " + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                               + " " + MB);
    }

    private boolean processFieldErrors(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        boolean hasFieldsErrors = bindingResult.hasErrors();
        if (hasFieldsErrors) {
            validationService.processFieldErrors(bindingResult, redirectAttributes);
        }
        return hasFieldsErrors;
    }

    private boolean isLoginExist(UserDTO userDTOFromModel, RedirectAttributes redirectAttributes, boolean hasErrors) {
        if (userService.isLoginExist(userDTOFromModel.getLogin())) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            hasErrors = true;
        }
        return hasErrors;
    }

}

