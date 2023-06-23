package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

//@WebServlet(name = "SignupServlet", value = Go.SIGNUP)
@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final ImageService imageService;

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
            Model model,
            RedirectAttributes redirectAttributes
    ) throws ServletException, IOException {

        if (userService.isLoginExist(login)) {
            redirectAttributes.addFlashAttribute("error", "Login already exist");
//            model.addAttribute(Key.ROLES, Role.values());
            return "redirect:signup";
        }

        User user = userService.build(
                login,
                password,
                role);

        User createdUser = userService.create(user).orElseThrow();
        imageService.uploadImage(request, user.getImage());
        request.getSession().setAttribute("user",createdUser);
        return "redirect:profile";
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        User user = userService.build(
                request.getParameter(Key.LOGIN),
                request.getParameter(Key.PASSWORD),
                request.getParameter(Key.ROLE));

        userService.create(user);
        imageService.uploadImage(request, user.getImage());
        Jsp.redirect(resp, Go.LOGIN);
    }
}
