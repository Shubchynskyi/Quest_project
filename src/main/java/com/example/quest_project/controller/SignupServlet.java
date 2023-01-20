package com.example.quest_project.controller;

import com.example.quest_project.entity.Role;
import com.example.quest_project.entity.User;
import com.example.quest_project.service.ImageService;
import com.example.quest_project.service.UserService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "SignupServlet", value = Go.SIGNUP)
@MultipartConfig(fileSizeThreshold = 1 << 20) // позволяет видеть файлы в запросах, fileSizeThreshold - размер файла,
// который не будет загружаться, а будет храниться в памяти, 1 << 20 - это 1 мегабайт
public class SignupServlet extends HttpServlet {

    @Override // это значит что на странице любого User будут доступны роли Role (user.jsp строка 51)
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute(Key.ROLES, Role.values());
        super.init(config);
    }

    UserService userService = UserService.USER_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsp.forward(req, resp, Key.SIGNUP);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // собираем пользователя из запроса
        User user = User.builder()
                .id(0L)
                .login(req.getParameter(Key.LOGIN))
                .password(req.getParameter(Key.PASSWORD))
                .role(Role.valueOf(req.getParameter(Key.ROLE)))
                .build();

        // получаем все параметры запроса и если они содержат команды - задаем логику
        Map<String, String[]> parameterMap = req.getParameterMap();

            userService.create(user);


        imageService.uploadImage(req, user.getId()); // загружаем аватар

        Jsp.redirect(resp, Key.USERS); // перенаправляем на всех пользователей
    }
}
