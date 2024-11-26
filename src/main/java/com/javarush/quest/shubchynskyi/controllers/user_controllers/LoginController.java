package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(LOGIN)
    public String showLoginPage() {
        return Route.LOGIN;
    }

    @PostMapping(LOGIN)
    public String doLogin(@RequestParam(LOGIN) String login,
                          @RequestParam(PASSWORD) String password,
                          @ModelAttribute(SOURCE) String source,
                          HttpSession session,
                          RedirectAttributes redirectAttributes
    ) {
        login = login.trim();
        password = password.trim();

        Optional<User> user = userService.get(login, password);
        if (user.isPresent()) {
            log.info("User {} logged in successfully.", login);
            session.setAttribute(
                    USER,
                    userMapper.userToUserDTOWithoutPassword(user.get())
            );
            if (source.isEmpty()) {
                return REDIRECT + Route.PROFILE;
            }
            return REDIRECT + source;
        } else {
            log.warn("Failed login attempt for user: {}", login);
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(DATA_IS_INCORRECT);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return REDIRECT + Route.LOGIN;
        }
    }
}