package com.example.quest_project.controller;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.User;
import com.example.quest_project.service.QuestService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

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
        //TODO Create Quest
        String questTitle = request.getParameter("name");
        String questText = request.getParameter("text");
//        System.out.printf("submit new quest, title - %s\n%s\n\n", questTitle, questText);
        Long userId = ((User)request.getSession().getAttribute("user")).getId();
        Quest quest = questService.parseQuestFromTextWall(questTitle, questText, userId);
        // после создания квеста нужен редирект на страницу редактирования для загрузки картинок
        // а оттуда редирект на список квестов

// паттерн post redirect get PRG, все post запросы должны заканчиваться редиректом
//если пользователя нет, то редирект на логин
    }
}
