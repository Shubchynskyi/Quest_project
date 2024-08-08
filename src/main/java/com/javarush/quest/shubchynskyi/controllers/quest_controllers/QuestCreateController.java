package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Set;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;

@Controller
@RequiredArgsConstructor
public class QuestCreateController {
    // Todo move to constant or to yaml
    public static final Set<Role> ALLOWED_ROLES_FOR_QUEST_CREATE = Set.of(Role.USER, Role.MODERATOR, Role.ADMIN);
    private final QuestService questService;
    private final ValidationService validationService;

    @GetMapping(CREATE_QUEST)
    public String showCreateQuestPage(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserDTO userDTO = (UserDTO) session.getAttribute(Key.USER);

        if (Objects.nonNull(userDTO)) {
            if (ALLOWED_ROLES_FOR_QUEST_CREATE.contains(userDTO.getRole())) {
                if (!model.containsAttribute(QUEST_DTO)) {
                    model.addAttribute(QUEST_DTO, QuestDTO.builder().build());
                }
                return CREATE_QUEST;
            } else {
                redirectAttributes.addFlashAttribute(ERROR, YOU_DONT_HAVE_PERMISSIONS);
                return REDIRECT + Route.PROFILE;
            }
        } else {
            redirectAttributes.addFlashAttribute(SOURCE, CREATE_QUEST);
            return REDIRECT + Route.LOGIN;
        }
    }

    @PostMapping(CREATE_QUEST)
    public String createQuest(
            @Valid @ModelAttribute(QUEST_DTO) QuestDTO questDTO,
            BindingResult bindingResult,
            @RequestParam(QUEST_TEXT) String questText,
            @RequestParam(ID) String authorId,
            RedirectAttributes redirectAttributes
    ) {
        boolean hasFieldsErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);
        if (hasFieldsErrors) {
            addQuestCreationDataToRedirectAttributes(questDTO, questText, redirectAttributes);
            return REDIRECT + CREATE_QUEST;
        }

        try {
            Quest quest = questService.create(questDTO.getName(), questText, questDTO.getDescription(), authorId);
            return REDIRECT + Route.QUEST_EDIT_ID + quest.getId();
        } catch (AppException e) {
            addQuestCreationDataToRedirectAttributes(questDTO, questText, redirectAttributes);
            var errorLocalizedMessage = ErrorLocalizer.getExceptionLocalizedMessage(e.getMessage());
            if (!errorLocalizedMessage.isMessageService()) {
                redirectAttributes.addFlashAttribute(ERROR, errorLocalizedMessage.message());
            } else {
                //TODO log
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
