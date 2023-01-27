package com.example.quest_project.controller;

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

//TODO редирект на страницу со списком квестом
@WebServlet(name = "QuestEditServlet", value = Go.QUESTS_EDIT)
public class QuestEditServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO получить квест и передать в реквест
        Jsp.forward(request, response, Key.QUEST_EDIT); // перейти на id квеста

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //после отправки данных об измененном квесте редирект на список квестов
        response.sendRedirect(Key.QUESTS_LIST);
    }
}
