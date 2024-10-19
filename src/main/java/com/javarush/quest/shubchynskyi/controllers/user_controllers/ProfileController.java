package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Slf4j
@Controller
public class ProfileController {

    // TODO список квестов пользователя с возможностью редактировать

    @GetMapping(PROFILE)
    public String showProfile(HttpSession session, Model model) {
        UserDTO userDTO = getUserFromSession(session);

        if (Objects.nonNull(userDTO)) {
            log.info("Displaying profile for user {} (ID: {}).", userDTO.getLogin(), userDTO.getId());
            List<QuestDTO> questDTOS = userDTO.getQuests();
            model.addAttribute(USER, userDTO);
            model.addAttribute(QUESTS, questDTOS);
            return Route.PROFILE;
        } else {
            log.warn("Access to profile denied: user not logged in.");
            return REDIRECT + Route.LOGIN;
        }
    }

    @PostMapping(PROFILE)
    public String processProfile(HttpSession session) {
        UserDTO user = getUserFromSession(session);

        if (Objects.nonNull(user)) {
            log.info("Processing profile for user {} (ID: {}).", user.getLogin(), user.getId());
            return REDIRECT + ID_URI_PATTERN.formatted(Route.USER, Objects.requireNonNull(user).getId());
        } else {
            log.warn("Profile update attempt without login.");
            return REDIRECT + Route.LOGIN;
        }
    }

    private UserDTO getUserFromSession(HttpSession session) {
        return Optional.ofNullable(session.getAttribute(USER))
                .filter(UserDTO.class::isInstance)
                .map(UserDTO.class::cast)
                .filter(user -> user.getId() != null)
                .orElse(null);
    }
}