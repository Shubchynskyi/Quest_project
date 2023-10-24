package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(USERS)
    public String showUsers(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (validateUserRole(session, redirectAttributes)) {
            return REDIRECT + Route.INDEX;
        }

        List<UserDTO> users = userService.getAll().stream()
                .map(u -> u.map(userMapper::userToUserDTOWithoutPasswordAndCollections))
                .flatMap(Optional::stream)
                .toList();

        model.addAttribute(USERS, users);
        return Route.USERS;
    }

    // TODO метод статический и используется двумя контроллерами
    // лучше вынести в какой-то класс userValidator или делать проверки через Secur
    static boolean validateUserRole(HttpSession session, RedirectAttributes redirectAttributes) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null
            || (!currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.MODERATOR))) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(YOU_DONT_HAVE_PERMISSIONS);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return true;
        }
        return false;
    }
}