package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.constant.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static com.javarush.quest.shubchynskyi.util.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.util.constant.Key.*;


@MultipartConfig(fileSizeThreshold = 1 << 20)
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
    public String signup(@ModelAttribute UserDTO userDTO,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes
    ) throws ServletException, IOException {

        if (isLoginExist(userDTO.getLogin())) {
            redirectAttributes.addFlashAttribute(ERROR, LOGIN_ALREADY_EXIST);
            return REDIRECT + Route.SIGNUP;
        }

        User createdUser = userService.create(userMapper.userDTOToUser(userDTO)).orElseThrow();
        imageService.uploadImage(request, createdUser.getImage());
        request.getSession()
                .setAttribute(USER, userMapper.userToUserDTOWithoutPassword(createdUser));
        return REDIRECT + Route.PROFILE;
    }

    private boolean isLoginExist(String login) {
        return userService.isLoginExist(login);
    }
}

