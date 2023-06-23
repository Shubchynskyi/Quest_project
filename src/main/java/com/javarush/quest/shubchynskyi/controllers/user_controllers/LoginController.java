package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

//@GetMapping("login")
//public String showLoginPage(){
//        return "login";
//        }
//
//@PostMapping("login")
//public String enterLogin(
//@RequestParam("login") String login,
//@RequestParam("password") String password
//        ){
//        Optional<User> user = userService.get(login, password);
//        if (user.isPresent()) {
//        HttpSession session = request.getSession();
//        session.setAttribute(Key.USER, user.get());
//        Jsp.redirect(response, Go.PROFILE);
//        } else {
//        Jsp.redirect(response, Go.LOGIN);
//        }
//        return "login";
//        }

@Controller
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

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
            session.setAttribute(Key.USER, user.get());
            return "redirect:" + Go.PROFILE;
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Data is incorrect, please check your username and password");
            return Go.LOGIN;
        }
    }


}