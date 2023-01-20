package com.example.quest_project.controller;

import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "CreateQuestServlet", value = Go.CREATE_QUEST)
public class CreateQuestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jsp.forward(request, response, Key.CREATE_QUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO Create Quest
    }
}
