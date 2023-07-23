package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("users")
    public String showUsers(
            Model model,
            HttpSession session
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute("user");
        if (currentUser == null
            || (!currentUser.getRole().equals(Role.ADMIN)
                && !currentUser.getRole().equals(Role.MODERATOR))) {
            return "redirect:/";
        }

        List<UserDTO> users = userService.getAll().stream()
                .map(u -> u.map(userMapper::userToUserDTOWithoutPasswordAndCollections))
                .flatMap(Optional::stream)
                .toList();

        model.addAttribute(Key.USERS, users);
        return "users";
    }
}