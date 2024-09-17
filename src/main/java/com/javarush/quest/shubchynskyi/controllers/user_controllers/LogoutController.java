package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.javarush.quest.shubchynskyi.constant.Key.LOGOUT;
import static com.javarush.quest.shubchynskyi.constant.Key.USER;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;

@Slf4j
@Controller
public class LogoutController {

    @GetMapping(LOGOUT)
    public String logOut(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute(USER);
        if (user != null) {
            log.info("User {} (ID: {}) logged out.", user.getLogin(), user.getId());
        } else {
            log.warn("Unknown user session invalidated.");
        }
        session.invalidate();
        return REDIRECT + Route.INDEX;
    }
}

