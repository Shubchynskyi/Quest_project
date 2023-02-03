package com.example.quest_project.controller;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@WebServlet(name = "CreateQuestServlet", value = Go.CREATE_QUEST)
public class CreateQuestServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Jsp.isParameterPresent(request, Key.USER)) {
            Jsp.forward(request, response, Key.CREATE_QUEST);
        } else {
            response.sendRedirect(Key.LOGIN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String questTitle = request.getParameter("name");
        String questText = request.getParameter("text");
        String questDescription = request.getParameter("description");
        //TODO если пользователя нет, то редирект на логин
        Long userId = ((User) request.getSession().getAttribute("user")).getId();
        Quest quest = questService.create(questTitle, questText, questDescription, userId);

//        Quest quest = questService.parseQuestFromTextWall(questTitle, questText, questDescription, userId);
//        request.getSession().setAttribute("quest", quest);
//        response.sendRedirect(Key.QUEST_EDIT);
//        Collection<Question> questions = quest.getQuestions();
//        request.setAttribute("questions", questions);
//        request.setAttribute("quest", quest);


        Jsp.redirect(response, Go.QUEST_EDIT + "?id=" + quest.getId());
    }
}
