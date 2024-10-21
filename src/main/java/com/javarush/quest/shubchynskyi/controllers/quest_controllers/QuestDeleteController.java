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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private final List<Role> acceptedRoles = List.of(Role.ADMIN, Role.MODERATOR);

    @PostMapping("quest-delete")
    public String deleteQuest(
            String id,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            String source   // источник, откуда пришел пользователь,
            // скрытое поле, которое надо для понимания того куда вернуть пользователя после удаления
    ) {
        UserDTO currentUserDTO = (UserDTO) session.getAttribute(USER);
        Optional<Quest> questOptional = questService.get(id);

        // Если квест пустой
        if (questOptional.isEmpty()) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_NOT_FOUND_ERROR);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Quest not found with ID: {}", id);
            return REDIRECT + source;
        }

        // Проверяем пользователя и его права
        if (Objects.nonNull(currentUserDTO)) { // todo проверяем что пользователь не null
            Quest quest = questOptional.get();
            Long authorId = quest.getAuthor().getId();

            // если пользователь не имеет прав, то вернем на source с ошибкой
            // todo проверяем что пользователь не null еще раз???
            if (validationService.checkUserAccessDenied(session, acceptedRoles, redirectAttributes)
                    && !Objects.equals(currentUserDTO.getId(), authorId)) {
                log.warn("Access denied to quest delete: insufficient permissions. Quest ID: {}. User ID: {}", id, currentUserDTO.getId());
                return REDIRECT + source;
            }

            try {
                questService.delete(quest);

                List<QuestDTO> quests = userService.get(currentUserDTO.getId())
                        .map(User::getQuests)
                        .orElseThrow(() -> new AppException(QUEST_LIST_ERROR))
                        .stream().map(questMapper::questToQuestDTOWithOutQuestions)
                        .toList();

                currentUserDTO.setQuests(quests);
                session.setAttribute(USER, currentUserDTO);
                log.info("Quest remove successful with ID: {}", id);
            } catch (AppException e) {
                String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_DELETE_QUEST_LIST_INCORRECT);
                redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
                log.warn("User or quests list not found, ID: {}", currentUserDTO.getId());
            }

            return "redirect:" + source;

        } else {
            log.warn("User is not logged in, redirecting to quests list page.");
            return REDIRECT + Route.QUESTS_LIST;
        }

    }

}