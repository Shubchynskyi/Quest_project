package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Collection;

//@WebServlet(name = "QuestsListServlet", value = Go.QUESTS_LIST)
@Controller
@RequiredArgsConstructor
public class QuestsListServlet {

    private final QuestService questService;

    @GetMapping("quests-list")
    public String showQuests(Model model) {
        Collection<Quest> quests = questService.getAll();
        model.addAttribute("quests", quests);
        return "quests-list";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Quest> quests = questService.getAll();
        request.setAttribute(Key.QUESTS, quests);
        Jsp.forward(request, response, Go.QUESTS_LIST);
    }
}
