package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Controller
public class ProfileController {

    @GetMapping(PROFILE)
    public String showProfile(HttpSession session, Model model) {
        UserDTO user = getUserFromSession(session);
        if (user == null) {
            return REDIRECT + Route.LOGIN;
        }

        model.addAttribute(USER, user);
        return Route.PROFILE;
    }

    @PostMapping(PROFILE)
    public String processProfile(HttpSession session) {
        UserDTO user = getUserFromSession(session);
        if (user == null) {
            return REDIRECT + Route.LOGIN;
        }
        return REDIRECT + ID_URI_PATTERN.formatted(Route.USER, Objects.requireNonNull(user).getId());
    }

    private UserDTO getUserFromSession(HttpSession session) {
        return Optional.ofNullable(session.getAttribute(USER))
                .filter(UserDTO.class::isInstance)
                .map(UserDTO.class::cast)
                .filter(user -> user.getId() != null)
                .orElse(null);
    }
}
