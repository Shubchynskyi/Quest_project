package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

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
}
