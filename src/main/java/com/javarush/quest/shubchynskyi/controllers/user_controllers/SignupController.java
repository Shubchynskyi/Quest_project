package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @GetMapping("signup")
    public String showSignup(Model model) {
        model.addAttribute(Key.ROLES, Role.values());
        return "signup";
    }

    @PostMapping("signup")
    public String signup(
            @RequestParam(Key.LOGIN) String login,
            @RequestParam(Key.PASSWORD) String password,
            @RequestParam(Key.ROLE) String role,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) throws ServletException, IOException {

        if (userService.isLoginExist(login)) {
            redirectAttributes.addFlashAttribute("error",
                    "Login already exist");
            return "redirect:signup";
        }

        User user = userService.build(
                login,
                password,
                role);

        User createdUser = userService.create(user).orElseThrow();
        imageService.uploadImage(request, user.getImage());
        request.getSession()
                .setAttribute("user", userMapper.userToUserDTOWithoutPassword(createdUser));
        return "redirect:profile";
    }
}
