package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.util.constant.Route;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.javarush.quest.shubchynskyi.util.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.util.constant.Key.LOGOUT;

@Controller
public class LogoutController {

    @GetMapping(LOGOUT)
    public String logOut(HttpSession session) {
        session.invalidate();
        return REDIRECT + Route.INDEX;
    }
}
