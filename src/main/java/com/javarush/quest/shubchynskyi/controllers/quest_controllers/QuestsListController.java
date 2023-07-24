package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.util.constant.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.util.constant.Key.QUESTS;
import static com.javarush.quest.shubchynskyi.util.constant.Key.QUESTS_LIST;

@Controller
@RequiredArgsConstructor
public class QuestsListController {

    private final QuestService questService;
    private final QuestMapper questMapper;

    @GetMapping(QUESTS_LIST)
    public String showQuests(Model model) {
        List<QuestDTO> questDTOS = questService.getAll().stream()
                .map(q -> q.map(questMapper::questToQuestDTOWithOutQuestions))
                .flatMap(Optional::stream)
                .toList();
        model.addAttribute(QUESTS, questDTOS);
        return Route.QUESTS_LIST;
    }
}
