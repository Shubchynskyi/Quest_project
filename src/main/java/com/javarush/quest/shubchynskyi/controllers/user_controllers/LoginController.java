package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;

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
                          HttpSession session,
                          RedirectAttributes redirectAttributes
    ) {
        Optional<User> user = userService.get(login, password);
        if (user.isPresent()) {
            session.setAttribute(
                    USER,
                    userMapper.userToUserDTOWithoutPassword(user.get())
            );
            return REDIRECT + Route.PROFILE;
        } else {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(DATA_IS_INCORRECT);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return REDIRECT + Route.LOGIN;
        }
    }

}