package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpServletRequest;
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


@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @GetMapping(SIGNUP)
    public String showSignup(Model model) {
        model.addAttribute(ROLES, Role.values());
        return Route.SIGNUP;
    }

    @PostMapping(SIGNUP)
    public String signup(@ModelAttribute UserDTO userDTOFromView,
                         @RequestParam(IMAGE) MultipartFile imageFile,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes
    ) throws IOException {

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            redirectAttributes.addFlashAttribute(
                    ERROR, FILE_IS_TOO_LARGE_MAXIMUM_SIZE_IS
                           + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                           + MB);
            return REDIRECT + Route.SIGNUP;
        }

        if (isLoginExist(userDTOFromView.getLogin())) {
            redirectAttributes.addFlashAttribute(ERROR, LOGIN_ALREADY_EXIST);
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

