package com.example.quest_project.controller;

import com.example.quest_project.entity.User;
import com.example.quest_project.service.QuestService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CreateQuestServlet", value = Go.CREATE_QUEST)
public class CreateQuestServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jsp.forward(request, response, Key.CREATE_QUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String questTitle = request.getParameter("name");
        String questText = request.getParameter("text");
        //TODO если пользователя нет, то редирект на логин
        Long userId = ((User)request.getSession().getAttribute("user")).getId();
        questService.parseQuestFromTextWall(questTitle, questText, userId);


        response.sendRedirect(Key.QUEST_EDIT);
//        Jsp.forward(request, response, Key.QUESTS_EDIT); // TODO передать id квеста
    }
}
