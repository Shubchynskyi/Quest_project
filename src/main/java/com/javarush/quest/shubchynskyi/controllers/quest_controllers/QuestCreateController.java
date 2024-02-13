package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.service.QuestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Set;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;

@Controller
@RequiredArgsConstructor
public class QuestCreateController {

    public static final Set<Role> ALLOWED_ROLES_FOR_QUEST_CREATE = Set.of(Role.USER, Role.MODERATOR, Role.ADMIN);
    private final QuestService questService;

    @GetMapping(CREATE_QUEST)
    public String showCreateQuestPage(
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        UserDTO userDTO = (UserDTO) session.getAttribute(Key.USER);

        if (Objects.nonNull(userDTO)) {
            if (ALLOWED_ROLES_FOR_QUEST_CREATE.contains(userDTO.getRole())) {
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
            @RequestParam(QUEST_NAME) String questName,
            @RequestParam(QUEST_TEXT) String questText,
            @RequestParam(QUEST_DESCRIPTION) String questDescription,
            @RequestParam(ID) String userId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Quest quest = questService.create(questName, questText, questDescription, userId);
            return REDIRECT + Route.QUEST_EDIT_ID + quest.getId();
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return REDIRECT + CREATE_QUEST;
        }
    }
}
