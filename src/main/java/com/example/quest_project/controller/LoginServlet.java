package com.example.quest_project.controller;

import com.example.quest_project.entity.User;
import com.example.quest_project.service.UserService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", value = Go.LOGIN)
public class LoginServlet extends HttpServlet {

    private final UserService userService = UserService.USER_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jsp.forward(request, response, Go.LOGIN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // форма логирования
        // читаем логин и пароль и получаем опшионал юзера
        String login = request.getParameter(Key.LOGIN);
        String password = request.getParameter(Key.PASSWORD);
        Optional<User> user = userService.get(login, password);

        // если пользователь есть
        if (user.isPresent()) {
            // то создаем новую сессию
            HttpSession session = request.getSession();
            // добавляем атрибут user и значение - полученный объект User
            session.setAttribute(Key.USER, user.get());
            // редирект на профиль пользователя
            Jsp.redirect(response, Go.PROFILE);
        } else {
            // если пользователя нет (неверный пароль), то редирект на страницу логина
            Jsp.redirect(response, Go.LOGIN); //TODO add error message
        }


    }
}
