package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    // Todo move to constant or to yaml
    protected static final List<Role> ALLOWED_ROLES_FOR_USERS_LIST =
            List.of(Role.MODERATOR, Role.ADMIN);

    @GetMapping(USERS)
    public String showUsers(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (validationService.checkUserAccessDenied(session, ALLOWED_ROLES_FOR_USERS_LIST, redirectAttributes)) {
            log.warn("Access denied to user list: insufficient permissions.");
            return REDIRECT + Route.INDEX;
        }

        List<UserDTO> users = userService.getAll().stream()
                .map(u -> u.map(userMapper::userToUserDTOWithoutPasswordAndCollections))
                .flatMap(Optional::stream)
                .toList();

        log.info("Displaying users list with {} users.", users.size());
        model.addAttribute(USERS, users);
        return Route.USERS;
    }
}