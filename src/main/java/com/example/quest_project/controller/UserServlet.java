package com.example.quest_project.controller;

import com.example.quest_project.entity.Role;
import com.example.quest_project.entity.User;
import com.example.quest_project.service.ImageService;
import com.example.quest_project.service.UserService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@WebServlet(name = "UserServlet", value = Go.USER)
@MultipartConfig(fileSizeThreshold = 1 << 20) // позволяет видеть файлы в запросах, fileSizeThreshold - размер файла,
// который не будет загружаться, а будет храниться в памяти, 1 << 20 - это 1 мегабайт
public class UserServlet extends HttpServlet {

    @Override // это значит что на странице любого User будут доступны роли Role (user.jsp строка 51)
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute(Key.ROLES, Role.values());
        super.init(config);
    }

    UserService userService = UserService.USER_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parameterId = req.getParameter(Key.ID);

        HttpSession session = req.getSession(); // сессия

        req.setAttribute(Key.ID, parameterId);
        if (Objects.nonNull(parameterId)) {
            long id = Long.parseLong(parameterId);
            Optional<User> optionalUser = userService.get(id);
            if (optionalUser.isPresent()) {         // дальше идем только если Optional что-то вернул, т.е. если получили User
                // теперь точно есть пользователь и можно дальше с ним работать
                User user = optionalUser.get();
                // добавляем пользователя в запрос
                req.setAttribute(Key.USER, user);
            }
            Jsp.forward(req, resp, Key.USER);
        }
        // короткий вариант с редиректом
        Jsp.redirect(resp, Key.USERS);

//                // иначе (если юзера нет) - получаем всех пользователей, добавляем в запрос и перенаправляем на страницу пользователей
//                Collection<User> users = userService.getAll();
//                req.setAttribute("users", users);
//                RequestDispatcher requestDispatcher = req.getRequestDispatcher("WEB-INF/users.jsp");
//                requestDispatcher.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> parameterMap1 = req.getParameterMap();
        for (var var : parameterMap1.entrySet()) {
            System.out.println(var.getKey());
            System.out.println(Arrays.toString(var.getValue()));
            System.out.println();
        }
        // собираем пользователя из запроса
        User user = User.builder()
                .id(Long.valueOf(req.getParameter(Key.ID)))
                .login(req.getParameter(Key.LOGIN))
                .password(req.getParameter(Key.PASSWORD))
                .role(Role.valueOf(req.getParameter(Key.ROLE)))
                .build();

        // получаем все параметры запроса и если они содержат команды - задаем логику
        Map<String, String[]> parameterMap = req.getParameterMap();
        if(parameterMap.containsKey("create")) {
            userService.create(user);
        } else if(parameterMap.containsKey("update")){
            userService.update(user);
        } else if(parameterMap.containsKey("delete")){
            userService.delete(user);
        } else throw new IllegalArgumentException("Unknown command"); //TODO some application exception

        imageService.uploadImage(req, user.getImage()); // загружаем аватар

        resp.sendRedirect(Key.USERS); // перенаправляем на всех пользователей
    }
}
