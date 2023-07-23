package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("login")
    public String showLoginPage() {
        return Go.LOGIN;
    }

    @PostMapping("login")
    public String doLogin(@RequestParam(Key.LOGIN) String login,
                          @RequestParam(Key.PASSWORD) String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes
    ) {
        Optional<User> user = userService.get(login, password);
        if (user.isPresent()) {
            session.setAttribute(
                    Key.USER,
                    userMapper.userToUserDTOWithoutPassword(user.get())
            );
            return "redirect:" + Go.PROFILE;
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Data is incorrect, please check your username and password");
            return Go.LOGIN;
        }
    }

}