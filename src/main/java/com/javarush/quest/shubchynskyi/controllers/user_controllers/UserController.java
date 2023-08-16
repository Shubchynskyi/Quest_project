package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;

@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @GetMapping(USER)
    public String showUser(
            Model model,
            HttpSession session,
            @RequestParam(value = ID, required = false) Long id,
            @RequestParam(value = SOURCE, required = false) String source,
            @SessionAttribute(name = USER, required = false) UserDTO userFromSession
    ) {
        if (Objects.nonNull(id)) {
            model.addAttribute(ROLES, Role.values());

            if (Objects.nonNull(source)) {
                session.setAttribute(SOURCE, source);
            }

            if (userFromSession != null) {
                UserDTO userDTO = getUserDTOWithPassword(userFromSession);

                if (userDTO.getId().equals(id)) {
                    model.addAttribute(USER, userDTO);
                } else {
                    addUserDtoToModel(model, id);
                }
            } else {
                addUserDtoToModel(model, id);
            }

            return Route.USER;
        } else {
            return REDIRECT + Route.USERS;
        }
    }

    private void addUserDtoToModel(Model model, Long id) {
        Optional<User> optionalUser = userService.get(id);
        optionalUser.ifPresent(value -> model.addAttribute(
                USER,
                userMapper.userToUserDTOWithoutCollections(value))
        );
    }

    @PostMapping(USER)
    public String editUser(
            @ModelAttribute UserDTO userDTOFromModel,
            @SessionAttribute(name = USER, required = false) UserDTO userDTOFromSession,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) throws ServletException, IOException {
        if (userDTOFromSession == null) {
            return REDIRECT + Route.LOGIN;
        }
        if (!isUserPermitted(userDTOFromModel, userDTOFromSession)) {
            return REDIRECT + Route.PROFILE;
        }

        userDTOFromSession = getUserDTOWithPassword(userDTOFromSession);
        User userFromModel = userMapper.userDTOToUser(userDTOFromModel);

        String redirectUrl = performUserActionAndResolveRedirect(userDTOFromSession, redirectAttributes, request, userFromModel);
        if (redirectUrl != null) {
            return redirectUrl;
        }
        imageService.uploadImage(request, userFromModel.getImage());
        return handleAdminFlow(request, userDTOFromSession, userFromModel);
    }

    private UserDTO getUserDTOWithPassword(UserDTO userDTOFromSession) {
        return userService.get(userDTOFromSession.getId())
                .map(userMapper::userToUserDTOWithoutCollections)
                .orElseThrow();
    }

    private static boolean isUserPermitted(UserDTO userDTOFromModel, UserDTO userDTOFromSession) {
        if (!userDTOFromSession.getId().equals(userDTOFromModel.getId())) {
            Role userRole = userDTOFromSession.getRole();
            return userRole.equals(Role.ADMIN) || userRole.equals(Role.MODERATOR);
        }
        return true;
    }

    private String performUserActionAndResolveRedirect(
            UserDTO userDTOFromSession,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            User userFromModel) {

        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap.containsKey(CREATE)) {
            userService.create(userFromModel);
        } else if (parameterMap.containsKey(UPDATE)) {
            User originalUser = userService.get(userFromModel.getId()).orElseThrow();

            String loginRedirect = checkExistingLogin(redirectAttributes, userFromModel, originalUser);
            if (loginRedirect != null) {
                return loginRedirect;
            }
            userService.update(userFromModel);
        } else if (parameterMap.containsKey(DELETE)) {
            userService.delete(userFromModel);
            if (userFromModel.getId().equals(userDTOFromSession.getId())) {
                return REDIRECT + Route.LOGOUT;
            } else {
                return REDIRECT + Route.USERS;
            }
        } else throw new AppException(UNKNOWN_COMMAND);
        return null;
    }

    private String checkExistingLogin(RedirectAttributes redirectAttributes, User userFromModel, User originalUser) {
        if (!originalUser.getLogin().equals(userFromModel.getLogin())
            && userService.isLoginExist(userFromModel.getLogin())) {
            redirectAttributes.addFlashAttribute(
                    ERROR, LOGIN_ALREADY_EXIST
            );
            return REDIRECT + Route.USER_ID + userFromModel.getId();
        }
        return null;
    }

    private String handleAdminFlow(HttpServletRequest request, UserDTO userDTOFromSession, User userFromModel) {
        if (userDTOFromSession.getId().equals(userFromModel.getId())) {
            updateUserSession(request, userFromModel);
        }
        if (!userDTOFromSession.getRole().equals(Role.ADMIN)) {
            return REDIRECT + Route.PROFILE;
        } else {
            String source = (String) request.getSession().getAttribute(SOURCE);
            request.getSession().removeAttribute(SOURCE);
            return source != null ? REDIRECT + source : REDIRECT + Route.PROFILE;
        }
    }

    private void updateUserSession(HttpServletRequest request, User userFromModel) {
        request.getSession()
                .setAttribute(
                        USER, userMapper.userToUserDTOWithoutPassword(userFromModel)
                );
    }
}
