package com.example.quest_project.controller;

import java.io.*;
import java.util.Collection;

import com.example.quest_project.entity.User;
import com.example.quest_project.service.UserService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "UsersServlet", value = Go.USERS)
public class UsersServlet extends HttpServlet {

    private final UserService userService = UserService.USER_SERVICE;

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Collection<User> users = userService.getAll();
        req.setAttribute(Key.USERS, users);    // контекст реквеста, самый частый, передали список пользователей
        // дальше нужно перенаправить наш запрос на jsp страницу
        Jsp.forward(req, resp, Key.USERS);

//        HttpSession session = req.getSession(); // сессия живет около 15 минут, привяза к пользователю
//        session.setAttribute("user", users);
//
//        ServletContext servletContext = req.getServletContext(); // доступно всем, часто хранятся к примеру енамы, что-то что не поменяется
    }



    public void destroy() {
    }
}