package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.constant.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.QUESTS;
import static com.javarush.quest.shubchynskyi.constant.Key.QUESTS_LIST;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestsListController {

    private final QuestService questService;
    private final QuestMapper questMapper;

    @GetMapping(QUESTS_LIST)
    public ModelAndView showQuests() {
        List<QuestDTO> questDTOS = questService.getAll().stream()
                .map(q -> q.map(questMapper::questToQuestDTOWithOutQuestions))
                .flatMap(Optional::stream)
                .toList();

        if (questDTOS.isEmpty()) {
            log.warn("No quests found to display.");
        } else {
            log.info("Displaying {} quests.", questDTOS.size());
        }

        ModelAndView modelAndView = new ModelAndView(Route.QUESTS_LIST);
        modelAndView.addObject(QUESTS, questDTOS);

        return modelAndView;
    }
}
