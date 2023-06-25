package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Objects;

//@WebServlet(name = "CreateQuestServlet", value = Go.CREATE_QUEST)
@Controller
@RequiredArgsConstructor
public class QuestCreateController {

    private final QuestService questService;

    @GetMapping("create-quest")
    public String showCreateQuestPage(
            HttpSession session
    ) {
        if (Objects.nonNull(session.getAttribute("user"))) {
            return "create-quest";
        } else {
            // TODO нужно передавать метку, чтобы сразу после ввода логина и пароля
            //  попадать не в профиль а на создание квеста
            return "redirect:login";
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Jsp.isParameterPresent(request, Key.USER)) {
            Jsp.forward(request, response, Go.CREATE_QUEST);
        } else {
            Jsp.redirect(response, Go.LOGIN);
        }
    }

    @PostMapping("create-quest")
    public String createQuest(
            @RequestParam(Key.QUEST_NAME) String questName,
            @RequestParam(Key.QUEST_TEXT) String questText,
            @RequestParam(Key.QUEST_DESCRIPTION) String questDescription,
            @RequestParam("id") String userId
    ){
        Quest quest = questService.create(questName, questText, questDescription, userId);
        return "redirect:quest-edit?id="+quest.getId(); // TODO check
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String questTitle = request.getParameter(Key.QUEST_NAME);
        String questText = request.getParameter(Key.QUEST_TEXT);
        String questDescription = request.getParameter(Key.QUEST_DESCRIPTION);
        String userId = (String )request.getSession().getAttribute("id");
        if (Objects.nonNull(userId)) {
            Quest quest = questService.create(questTitle, questText, questDescription, userId);
            Jsp.redirect(response, Key.ID_URI_PATTERN.formatted(Go.QUEST_EDIT, quest.getId()));
        } else {
            Jsp.redirect(response, Go.LOGIN);
        }
    }
}
