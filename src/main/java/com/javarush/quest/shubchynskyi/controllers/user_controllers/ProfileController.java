package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

//@WebServlet(name = "ProfileServlet", value = Go.PROFILE)
@Controller
public class ProfileController {

    @GetMapping("profile")
    public String showProfile(){
        return "profile";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jsp.forward(request, response, Go.PROFILE);
    }

    @PostMapping("profile")
    public String processProfile(HttpSession session) {
        User user = (User) session.getAttribute(Key.USER);
        return "redirect:" + Key.ID_URI_PATTERN.formatted(Go.USER, user.getId());
    }

}
