package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "CreateQuestServlet", value = Go.CREATE_QUEST)
public class QuestCreateServlet extends HttpServlet {

    private QuestService questService;

    @Autowired
    public void setQuestService(QuestService questService) {
        this.questService = questService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Jsp.isParameterPresent(request, Key.USER)) {
            Jsp.forward(request, response, Go.CREATE_QUEST);
        } else {
            Jsp.redirect(response, Go.LOGIN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String questTitle = request.getParameter(Key.QUEST_NAME);
        String questText = request.getParameter(Key.QUEST_TEXT);
        String questDescription = request.getParameter(Key.QUEST_DESCRIPTION);
        Long userId = ((User) request.getSession().getAttribute(Key.USER)).getId();
        if (Objects.nonNull(userId)) {
            Quest quest = questService.create(questTitle, questText, questDescription, userId);
            Jsp.redirect(response, Key.ID_URI_PATTERN.formatted(Go.QUEST_EDIT, quest.getId()));
        } else {
            Jsp.redirect(response, Go.LOGIN);
        }
    }
}
