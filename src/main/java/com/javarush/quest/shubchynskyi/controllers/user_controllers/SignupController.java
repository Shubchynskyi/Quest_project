package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.LOGIN_ALREADY_EXIST;


@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @GetMapping(SIGNUP)
    public String showSignup(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null) {
            model.addAttribute(ROLES, Role.values());
            return Route.SIGNUP;
        } else {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(YOU_ARE_ALREADY_LOGGED_IN);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return REDIRECT + Route.PROFILE;
        }

    }

    @PostMapping(SIGNUP)
    public String signup(@ModelAttribute UserDTO userDTOFromView,
                         @RequestParam(IMAGE) MultipartFile imageFile,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes
    ) throws IOException {

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(FILE_IS_TOO_LARGE);
            redirectAttributes.addFlashAttribute(
                    ERROR, localizedMessage
                           + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                           + MB);
            return REDIRECT + Route.SIGNUP;
        }

        if (isLoginExist(userDTOFromView.getLogin())) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return REDIRECT + Route.SIGNUP;
        }

        User user = userMapper.userDTOToUser(userDTOFromView);
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

