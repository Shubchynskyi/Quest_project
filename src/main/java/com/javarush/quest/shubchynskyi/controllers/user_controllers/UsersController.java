package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class UsersController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users")
    public String showUsers(
            Model model,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getRole().equals(Role.ADMIN)) {
            return "redirect:/";
        }

        Collection<User> users = userService.getAll();
        model.addAttribute(Key.USERS, users);
        return "users";
    }
}