package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Controller
public class ProfileController {

    @GetMapping(PROFILE)
    public String showProfile(HttpSession session, Model model) {
        Object userObject = session.getAttribute(USER);
        if (userObject == null) {
            return REDIRECT + Route.LOGIN;
        }

        UserDTO user = (UserDTO) userObject;
        model.addAttribute("user", user);

        return Route.PROFILE;
    }


    @PostMapping(PROFILE)
    public String processProfile(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute(USER);
        return REDIRECT + ID_URI_PATTERN.formatted(Route.USER, user.getId());
    }

}
