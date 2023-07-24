package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.constant.Route;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.util.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.util.constant.Key.USER;
import static com.javarush.quest.shubchynskyi.util.constant.Key.USERS;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(USERS)
    public String showUsers(
            Model model,
            HttpSession session
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null
            || (!currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.MODERATOR))) {
            return REDIRECT + Route.INDEX;
        }

        List<UserDTO> users = userService.getAll().stream()
                .map(u -> u.map(userMapper::userToUserDTOWithoutPasswordAndCollections))
                .flatMap(Optional::stream)
                .toList();

        model.addAttribute(USERS, users);
        return Route.USERS;
    }
}