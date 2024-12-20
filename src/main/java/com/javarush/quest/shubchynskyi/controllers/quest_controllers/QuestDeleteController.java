package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.config.RoleConfig;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_DELETE_QUEST_LIST_INCORRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_NOT_FOUND_ERROR;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestDeleteController {

    private final QuestService questService;
    private final UserService userService;
    private final ValidationService validationService;
    private final QuestMapper questMapper;

    @PostMapping(QUEST_DELETE)
    public String deleteQuest(
            @RequestParam Long id,
            @RequestParam String source,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserDTO currentUserDTO = (UserDTO) session.getAttribute(USER);

        if (currentUserDTO == null) {
            log.warn("User is not logged in, redirecting to quests list page.");
            return REDIRECT + Route.QUESTS_LIST;
        }

        Optional<Quest> questOptional = questService.get(id);

        if (questOptional.isEmpty()) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_NOT_FOUND_ERROR);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Quest not found with ID: {}", id);
            return REDIRECT + source;
        }

        Quest quest = questOptional.get();
        Long authorId = questService.getAuthorId(quest);

        if (validationService.checkUserAccessDenied(session, RoleConfig.ALLOWED_ROLES_FOR_QUEST_DELETE, redirectAttributes, authorId)) {
            log.warn("Access denied for quest deletion. Quest ID: {}. User ID: {}", id, currentUserDTO.getId());
            return REDIRECT + source;
        }

        try {
            questService.delete(quest);

            List<QuestDTO> quests = userService.get(currentUserDTO.getId())
                    .map(User::getQuests)
                    .orElseThrow(() -> new AppException(QUEST_LIST_ERROR))
                    .stream()
                    .map(questMapper::questToQuestDTOWithOutQuestions)
                    .collect(Collectors.toList());

            currentUserDTO.setQuests(quests);
            session.setAttribute(USER, currentUserDTO);
            log.info("Quest successfully deleted. Quest ID: {}", id);
        } catch (AppException e) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_DELETE_QUEST_LIST_INCORRECT);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Failed to update user's quest list after quest deletion. User ID: {}", currentUserDTO.getId(), e);
        }

        return REDIRECT + source;
    }

}