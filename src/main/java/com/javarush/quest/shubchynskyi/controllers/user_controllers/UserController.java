package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @GetMapping("user")
    public String showUser(
            Model model,
            HttpSession session,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "source", required = false) String source
    ) {
        if (Objects.nonNull(id)) {
            model.addAttribute(Key.ROLES, Role.values());

            if (Objects.nonNull(source)) {
                session.setAttribute("source", source);
            }

            model.addAttribute("source", source);

            if (session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                UserDTO userDTO = userMapper.userToUserDTOWithOutCollections(user);

                if (userDTO.getId().equals(id)) {
                    model.addAttribute(Key.USER, userDTO);
                } else {
                    addUserDtoToModel(model, id);
                }
            } else {
                addUserDtoToModel(model, id);
            }

            return "user";
        } else {
            return "redirect:users";
        }
    }

    private void addUserDtoToModel(Model model, Long id) {
        Optional<User> optionalUser = userService.get(id);
        optionalUser.ifPresent(value -> model.addAttribute(Key.USER, userMapper.userToUserDTOWithOutCollections(value)));
    }

    @PostMapping("user")
    public String editUser(
            @ModelAttribute UserDTO userDTO,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        UserDTO currentUserDTO = userMapper.userToUserDTOWithOutCollections(currentUser);

        if (currentUserDTO == null) {
            return "redirect:login";
        }

        boolean isCurrentUserAdmin = currentUserDTO.getRole().equals(Role.ADMIN);

        User user = userMapper.userDTOToUser(userDTO);

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey(Key.CREATE)) {
            userService.create(user);
        } else if (parameterMap.containsKey(Key.UPDATE)) {

            User originalUser = userService.get(userDTO.getId()).orElseThrow();

            if (!originalUser.getLogin().equals(userDTO.getLogin()) && userService.isLoginExist(userDTO.getLogin())) {
                    redirectAttributes.addFlashAttribute("error",
                            "Login already exist");
                    return "redirect:user?id=" + userDTO.getId();
            }

            userService.update(user);
        } else if (parameterMap.containsKey(Key.DELETE)) {
            userService.delete(user);
            return "redirect:logout";
        } else throw new AppException(Key.UNKNOWN_COMMAND);

        imageService.uploadImage(request, user.getImage());


        if (!isCurrentUserAdmin) {
            // current user(not admin) is editing his profile
            request.getSession().setAttribute("user", user);
            return "redirect:profile";
        } else {
            if (currentUserDTO.getId().equals(userDTO.getId())) {
                // admin edits his profile, update session
                request.getSession().setAttribute("user", user);
            }
            // admin edits user's profile from users list
            // source - the place where the admin came from and where he will return
            // (need when admin editing his profile from users list)
            String source = (String) request.getSession().getAttribute("source");
            request.getSession().removeAttribute("source");

            return source != null ? "redirect:" + source : "redirect:profile";
        }
    }
}
