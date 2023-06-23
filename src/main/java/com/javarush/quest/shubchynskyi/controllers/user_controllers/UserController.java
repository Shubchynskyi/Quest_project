package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

//@WebServlet(name = "UserServlet", value = Go.USER)
@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("user")
    public String showUser(
            Model model,
            HttpSession session,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "source", required = false) String source
    ) {
        if (Objects.nonNull(id)) {
            model.addAttribute(Key.ROLES, Role.values());

            if (Objects.nonNull(source)) {
                session.setAttribute("source", source);
            }

            model.addAttribute("source", source);

            if (session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                if (user.getId().equals(id)) {
                    model.addAttribute(Key.USER, user);
                } else {
                    Optional<User> optionalUser = userService.get(id);
                    optionalUser.ifPresent(value -> model.addAttribute(Key.USER, value));
                }
            } else {
                Optional<User> optionalUser = userService.get(id);
                optionalUser.ifPresent(value -> model.addAttribute(Key.USER, value));
            }

            return "user";
        } else {
            return "redirect:users";
        }
    }

    @PostMapping("user")
    public String editUser(
            @RequestParam(Key.ID) String id,
            @RequestParam(Key.LOGIN) String login,
            @RequestParam(Key.PASSWORD) String password,
            @RequestParam(Key.ROLE) String role,
            HttpServletRequest request
    ) throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null) {
            return "redirect:login";
        }

        boolean isCurrentUserAdmin = currentUser.getRole().equals(Role.ADMIN);

        User user = userService.build(
                id,
                login,
                password,
                role);

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey(Key.CREATE)) {
            userService.create(user);
        } else if (parameterMap.containsKey(Key.UPDATE)) {
            userService.update(user);
        } else if (parameterMap.containsKey(Key.DELETE)) {
            userService.delete(user);
        } else throw new AppException(Key.UNKNOWN_COMMAND);

        imageService.uploadImage(request, user.getImage());


        if (!isCurrentUserAdmin) {
            // текущий пользователь редактирует свой профиль
            request.getSession().setAttribute("user", user);
            return "redirect:profile";
        } else {
            if (currentUser.getId().equals(Long.parseLong(id))) {
                // админ редактирует свой профиль
                request.getSession().setAttribute("user", user);
            }
            String source = (String) request.getSession().getAttribute("source");
            request.getSession().removeAttribute("source");
            // администратор редактирует профиль другого пользователя
            return source != null ? "redirect:" + source : "redirect:profile";
        }
    }
}
