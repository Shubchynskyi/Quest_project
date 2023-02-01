package com.example.quest_project.controller;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.entity.User;
import com.example.quest_project.service.ImageService;
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
import java.util.Collection;

//TODO редирект на страницу со списком квестом
@WebServlet(name = "QuestsListServlet", value = Go.QUESTS_LIST)
public class QuestsListServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Quest> quests = questService.getAll();
        request.setAttribute("quests", quests);

        Jsp.forward(request, response, Key.QUESTS_LIST);
    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String questTitle = request.getParameter("name");
//        String questText = request.getParameter("text");
////        System.out.printf("submit new quest, title - %s\n%s\n\n", questTitle, questText);
//        Long userId = ((User)request.getSession().getAttribute("user")).getId();
//        questService.parseQuestFromTextWall(questTitle, questText, userId);
//        // после создания квеста нужен редирект на страницу редактирования для загрузки картинок
//        // а оттуда редирект на список квестов
//
//// паттерн post redirect get PRG, все post запросы должны заканчиваться редиректом
////если пользователя нет, то редирект на логин
//        Jsp.forward(request, response, Key.QUESTS_LIST);
//    }
}
