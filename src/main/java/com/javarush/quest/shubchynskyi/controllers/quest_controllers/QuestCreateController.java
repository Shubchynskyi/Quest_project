package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.config.RoleConfig;
import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuestCreateController {

    private final QuestService questService;
    private final ValidationService validationService;
    private final QuestMapper questMapper;

    @GetMapping(CREATE_QUEST)
    public String showCreateQuestPage(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserDTO userDTO = (UserDTO) session.getAttribute(Key.USER);

        if (Objects.nonNull(userDTO)) {
            if (RoleConfig.ALLOWED_ROLES_FOR_QUEST_CREATE.contains(userDTO.getRole())) {
                log.info("User {} with role {} is accessing the create quest page.", userDTO.getLogin(), userDTO.getRole());
                if (!model.containsAttribute(QUEST_DTO)) {
                    model.addAttribute(QUEST_DTO, QuestDTO.builder().build());
                }
                return CREATE_QUEST;
            } else {
                log.warn("Access denied for user {} with role {} when accessing the create quest page.", userDTO.getLogin(), userDTO.getRole());
                redirectAttributes.addFlashAttribute(ERROR, YOU_DONT_HAVE_PERMISSIONS);
                return REDIRECT + Route.PROFILE;
            }
        } else {
            log.info("User is not logged in, redirecting to login page.");
            redirectAttributes.addFlashAttribute(SOURCE, CREATE_QUEST);
            return REDIRECT + Route.LOGIN;
        }
    }

    @PostMapping(CREATE_QUEST)
    public String createQuest(
            @Valid @ModelAttribute(QUEST_DTO) QuestDTO questDTO,
            BindingResult bindingResult,
            @RequestParam(QUEST_TEXT) String questText,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        if (validationService.checkUserAccessDenied(session, RoleConfig.ALLOWED_ROLES_FOR_QUEST_CREATE, redirectAttributes)) {
            log.warn("Access denied to quest create controller: insufficient permissions.");
            return REDIRECT + Route.LOGIN;
        }

        boolean hasFieldsErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);
        if (hasFieldsErrors) {
            log.info("Quest creation failed due to validation errors for quest: {}", questDTO.getName());
            addQuestCreationDataToRedirectAttributes(questDTO, questText, redirectAttributes);
            return REDIRECT + CREATE_QUEST;
        }

        try {
            UserDTO userDTO = (UserDTO) session.getAttribute(USER);
            Quest quest = questService.create(questDTO.getName(), questText, questDTO.getDescription(), String.valueOf(userDTO.getId()));
            List<QuestDTO> quests = userDTO.getQuests() == null ? new ArrayList<>() : new ArrayList<>(userDTO.getQuests());
            quests.add(questMapper.questToQuestDTOWithOutQuestions(quest));
            userDTO.setQuests(quests);
            session.setAttribute(USER, userDTO);
            log.info("Quest created successfully with ID: {}", quest.getId());
            return REDIRECT + Route.QUEST_EDIT_ID + quest.getId();
        } catch (AppException e) {
            log.error("Error occurred while creating quest: {}", questDTO.getName(), e);
            addQuestCreationDataToRedirectAttributes(questDTO, questText, redirectAttributes);
            var errorLocalizedMessage = ErrorLocalizer.getExceptionLocalizedMessage(e.getMessage());
            if (!errorLocalizedMessage.isMessageService()) {
                redirectAttributes.addFlashAttribute(ERROR, errorLocalizedMessage.message());
            } else {
                log.error("Unexpected error occurred during quest creation: {}", e.getMessage());
                redirectAttributes.addFlashAttribute(ERROR, UNEXPECTED_ERROR);
            }
            return REDIRECT + CREATE_QUEST;
        }
    }

    private static void addQuestCreationDataToRedirectAttributes(QuestDTO questDTO, String questText, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(QUEST_TEXT, questText);
        redirectAttributes.addFlashAttribute(QUEST_DTO, questDTO);
    }

}