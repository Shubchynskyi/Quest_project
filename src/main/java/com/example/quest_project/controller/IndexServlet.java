package com.example.quest_project.controller;

import com.example.quest_project.entity.Role;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name  = "IndexServlet", value = Go.ROOT, loadOnStartup = 0) // пустой адрес "" - корень контекста, "/" - любое слово вписаное в URI будет приниматься сервлетом,
// "/*" - только те кто в этом каталоге
public class IndexServlet extends HttpServlet {

    @Override // это значит что на странице этого сервлета будут доступны роли Role (user.jsp строка 51)
    //TODO перенести в слушатель
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute(Key.ROLES, Role.values());
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsp.forward(req, resp, Key.INDEX);
    }
}
