package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @GetMapping("profile")
    public String showProfile() {
        return "profile";
    }

    @PostMapping("profile")
    public String processProfile(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute(Key.USER);
        return "redirect:" + Key.ID_URI_PATTERN.formatted(Go.USER, user.getId());
    }

}
