package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestDeleteController {

    private final QuestService questService;
    private final UserService userService;
    private final ValidationService validationService;
    private final QuestMapper questMapper;

    protected static final List<Role> ALLOWED_ROLES_FOR_QUEST_DELETE =
            List.of(Role.MODERATOR, Role.ADMIN);

    @PostMapping(QUEST_DELETE)
    public String deleteQuest(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam String source
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
        Long authorId = quest.getAuthor().getId();

        boolean accessDenied = validationService.checkUserAccessDenied(session, ALLOWED_ROLES_FOR_QUEST_DELETE, redirectAttributes)
                && !Objects.equals(currentUserDTO.getId(), authorId);

        if (accessDenied) {
            log.warn("Access denied for quest deletion. Quest ID: {}. User ID: {}", id, currentUserDTO.getId());
            redirectAttributes.addFlashAttribute(ERROR, YOU_DONT_HAVE_PERMISSIONS);
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