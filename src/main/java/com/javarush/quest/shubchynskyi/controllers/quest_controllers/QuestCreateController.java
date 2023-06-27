package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

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

    @PostMapping("create-quest")
    public String createQuest(
            @RequestParam(Key.QUEST_NAME) String questName,
            @RequestParam(Key.QUEST_TEXT) String questText,
            @RequestParam(Key.QUEST_DESCRIPTION) String questDescription,
            @RequestParam("id") String userId
    ) {
        Quest quest = questService.create(questName, questText, questDescription, userId);
        return "redirect:quest-edit?id=" + quest.getId();
    }
}
