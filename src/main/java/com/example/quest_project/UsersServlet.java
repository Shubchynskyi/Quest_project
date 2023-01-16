package com.example.quest_project;

import java.io.*;
import java.util.Collection;

import com.example.quest_project.entity.User;
import com.example.quest_project.service.UserService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "UsersServlet", value = "/users")
public class UsersServlet extends HttpServlet {

    private final UserService userService = UserService.USER_SERVICE;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Collection<User> users = userService.getAll();
        request.setAttribute("users", users);    // контекст реквеста, самый частый




//        HttpSession session = request.getSession(); // сессия живет около 15 минут, привяза к пользователю
//        session.setAttribute("user", users);
//
//        ServletContext servletContext = request.getServletContext(); // доступно всем, часто хранятся к примеру енамы, что-то что не поменяется
    }

    public void destroy() {
    }
}