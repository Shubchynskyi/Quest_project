package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestsListController {

    private final QuestService questService;
    private final QuestMapper questMapper;

    @GetMapping(QUESTS_LIST)
    public String showQuests(
            Model model,
            HttpSession session
    ) {
        List<QuestDTO> questDTOS = questService.getAll().stream()
                .map(q -> q.map(questMapper::questToQuestDTOWithOutQuestions))
                .flatMap(Optional::stream)
                .toList();

        if (questDTOS.isEmpty()) {
            log.warn("No quests found to display.");
        } else {
            log.info("Displaying {} quests.", questDTOS.size());
        }

        UserDTO currentUser = (UserDTO) session.getAttribute(USER);

        model.addAttribute(QUESTS, questDTOS);
        model.addAttribute(CURRENT_USER_ROLE, currentUser != null ? currentUser.getRole().toString() : null);
        return Route.QUESTS_LIST;
    }

}