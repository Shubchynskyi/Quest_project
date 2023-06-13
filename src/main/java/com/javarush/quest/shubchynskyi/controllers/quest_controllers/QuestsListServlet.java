package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
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
import java.util.Collection;

@WebServlet(name = "QuestsListServlet", value = Go.QUESTS_LIST)
public class QuestsListServlet extends HttpServlet {

    private QuestService questService;
    @Autowired
    public void setQuestService(QuestService questService) {
        this.questService = questService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Quest> quests = questService.getAll();
        request.setAttribute(Key.QUESTS, quests);
        Jsp.forward(request, response, Go.QUESTS_LIST);
    }
}
