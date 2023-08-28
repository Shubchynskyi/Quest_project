package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.service.QuestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;

@Controller
@RequiredArgsConstructor
public class QuestCreateController {

    private final QuestService questService;

    @GetMapping(CREATE_QUEST)
    public String showCreateQuestPage(
            HttpSession session
    ) {
        if (Objects.nonNull(session.getAttribute(Key.USER))) {
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
            @RequestParam(ID) String userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Quest quest = questService.create(questName, questText, questDescription, userId);
            return REDIRECT + Route.QUEST_EDIT_ID + quest.getId();
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return REDIRECT + CREATE_QUEST;
        }
    }
}
