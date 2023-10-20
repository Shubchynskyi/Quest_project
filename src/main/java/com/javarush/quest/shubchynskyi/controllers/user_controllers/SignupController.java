package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;


@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    @GetMapping(SIGNUP)
    public String showSignup(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute(name = "userDTOFromModel") UserDTO userDTOFromModel // TODO для сохранения данных
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null) {

            // TODO получаем данные и сохраняем в модель если они были ранее сохранены при редиректе
            if (userDTOFromModel != null) {
                model.addAttribute("userDTOFromModel", userDTOFromModel);
            }

            model.addAttribute(ROLES, Role.values());
            return Route.SIGNUP;
        } else {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(YOU_ARE_ALREADY_LOGGED_IN);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return REDIRECT + Route.PROFILE;
        }

    }

    @PostMapping(SIGNUP)
    public String signup(@Valid @ModelAttribute UserDTO userDTOFromModel,
                         BindingResult bindingResult,
                         @RequestParam(IMAGE) MultipartFile imageFile,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes
    ) throws IOException {

        boolean hasErrors = false;

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            Locale locale = LocaleContextHolder.getLocale();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String localizedErrorMessage = messageSource.getMessage(error, locale);
                errors.put(error.getField(), localizedErrorMessage);
            }
            redirectAttributes.addFlashAttribute(FIELD_ERRORS, errors);
            redirectAttributes.addFlashAttribute("userDTOFromModel", userDTOFromModel);
            hasErrors = true;
        }

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(FILE_IS_TOO_LARGE);
            redirectAttributes.addFlashAttribute(
                    IMAGING_ERROR, localizedMessage
                                   + " " + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                                   + " " + MB);
            hasErrors = true;
        }

        if (isLoginExist(userDTOFromModel.getLogin())) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            hasErrors = true;
        }

        if (hasErrors) {
            return REDIRECT + Route.SIGNUP;
        }

        User user = userMapper.userDTOToUser(userDTOFromModel);
        User createdUser = userService.create(user).orElseThrow();
        imageService.uploadImage(imageFile, createdUser.getImage());

        UserDTO userDTO = userMapper.userToUserDTOWithoutPassword(createdUser);
        request.getSession()
                .setAttribute(USER, userDTO);
        return REDIRECT + Route.PROFILE;
    }

    private boolean isLoginExist(String login) {
        return userService.isLoginExist(login);
    }
}

