package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("logout")
    public String logOut(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
