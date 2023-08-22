package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.constant.Key;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;


import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Controller
@RequiredArgsConstructor
public class QuestCreateController {

    private final QuestService questService;

    @GetMapping(CREATE_QUEST)
    public String showCreateQuestPage(
            Model model,
            HttpServletRequest request
    ) {
//        String currentUrl = request.getRequestURI(); // Получение текущего URI
//        model.addAttribute("path", currentUrl); // Добавление URI в модель
        model.addAttribute(Key.TITLE_KEY, "create-quest.title");
        if (Objects.nonNull(request.getSession().getAttribute(Key.USER))) {
            return CREATE_QUEST;
        } else {
            // TODO if the user was not logged in and clicked "create quest":
            //  - redirected to the login with the label "source" = "quest-create" (the label in the controller is optional
            //  - if the label is present, then after login redirect to create a quest
            return REDIRECT + Route.LOGIN;
        }
    }

    @PostMapping(CREATE_QUEST)
    public String createQuest(
            @RequestParam(QUEST_NAME) String questName,
            @RequestParam(QUEST_TEXT) String questText,
            @RequestParam(QUEST_DESCRIPTION) String questDescription,
            @RequestParam(ID) String userId
    ) {
        Quest quest = questService.create(questName, questText, questDescription, userId);
        // TODO if quest already exist (quest name) -> add error to flash and redirect
        return REDIRECT + Route.QUEST_EDIT_ID + quest.getId();
    }
}
