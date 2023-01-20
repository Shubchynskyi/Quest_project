package com.example.quest_project.controller;

import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name  = "IndexServlet", value = Go.ROOT) // пустой адрес "" - корень контекста, "/" - любое слово вписаное в URI будет приниматься сервлетом,
// "/*" - только те кто в этом каталоге
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsp.forward(req, resp, Key.INDEX);
    }
}
