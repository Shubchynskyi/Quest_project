package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.result.UserDataProcessResult;
import com.javarush.quest.shubchynskyi.dto.OnCreate;
import com.javarush.quest.shubchynskyi.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_ARE_ALREADY_LOGGED_IN;


@Slf4j
@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserAccountService userAccountService;

    @GetMapping(SIGNUP)
    public String showSignup(Model model,
                             @ModelAttribute(name = USER_DTO_FROM_MODEL) UserDTO userDTOFromModel,
                             @ModelAttribute(name = TEMP_IMAGE_ID) String tempImageId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes
    ) {
        if (isUserNotLoggedIn(session)) {
            log.info("Displaying signup page.");
            prepareSignupModel(model, userDTOFromModel, tempImageId);
            return Route.SIGNUP;
        } else {
            log.warn("User already logged in attempted to access signup page.");
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
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(YOU_ARE_ALREADY_LOGGED_IN);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
    }

    private boolean isUserNotLoggedIn(HttpSession session) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        return currentUser == null;
    }

    @PostMapping(SIGNUP)
    public String signup(@ModelAttribute @Validated(OnCreate.class) UserDTO userDTOFromModel,
                         BindingResult bindingResult,
                         @RequestParam(name = IMAGE, required = false) MultipartFile imageFile,
                         @RequestParam(name = TEMP_IMAGE_ID, required = false) String tempImageId,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {

        try {
            UserDataProcessResult registrationResult = userAccountService.processUserData(
                    userDTOFromModel,
                    bindingResult,
                    imageFile,
                    tempImageId,
                    redirectAttributes,
                    EMPTY_STRING
            );

            if (registrationResult.hasFieldsErrors()) {
                log.warn("Validation errors during signup for user: {}", userDTOFromModel.getLogin());
                redirectAttributes.addFlashAttribute(USER_DTO_FROM_MODEL, userDTOFromModel);
                return REDIRECT + Route.SIGNUP;
            }

            UserDTO userDTO = userAccountService.registerNewUser(
                    userDTOFromModel,
                    imageFile,
                    registrationResult.tempImageId(),
                    registrationResult.isTempImagePresent(),
                    registrationResult.imageIsValid());

            log.info("User registered successfully with login: {}", userDTO.getLogin());
            request.getSession().setAttribute(USER, userDTO);
        } catch (AppException e) {
            log.error("Error during signup: {}", e.getMessage(), e);
            addLocalizedUnexpectedError(redirectAttributes);
            return REDIRECT + Route.INDEX;
        }
        return REDIRECT + Route.PROFILE;
    }

    private void addLocalizedUnexpectedError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
    }
}