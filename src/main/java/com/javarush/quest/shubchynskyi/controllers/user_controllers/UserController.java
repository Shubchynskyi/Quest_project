package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.result.UserDataProcessResult;
import com.javarush.quest.shubchynskyi.service.UserAccountService;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.service.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ValidationService validationService;
    private final UserAccountService userAccountService;

    private final List<Role> acceptedRolesForEditing = List.of(Role.ADMIN, Role.MODERATOR);

    @GetMapping(USER)
    public String showUser(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = ID, required = false) Long id,
            @RequestParam(value = SOURCE, required = false) String source,
            @SessionAttribute(name = USER, required = false) UserDTO userFromSession,
            @ModelAttribute(name = TEMP_IMAGE_ID) String tempImageId
    ) {
        if (validationService.checkUserAccessDenied(session, acceptedRolesForEditing, redirectAttributes)) {
            return REDIRECT + Route.INDEX;
        }

        if (Objects.nonNull(id)) {
            model.addAttribute(ROLES, Role.values());

            if (Objects.nonNull(source)) {
                session.setAttribute(SOURCE, source);
            }

            if (tempImageId != null && !tempImageId.isEmpty()) {
                model.addAttribute(TEMP_IMAGE_ID, tempImageId);
            }

            UserDTO userDTO;

            if (userFromSession != null && userFromSession.getId().equals(id)) {
                userDTO = userService.get(userFromSession.getId())
                        .map(userMapper::userToUserDTOWithoutCollections)
                        .orElseThrow();
                model.addAttribute(USER, userDTO);
                session.setAttribute(ORIGINAL_LOGIN, userDTO.getLogin());
            } else {
                userDTO = addUserDtoToModel(model, id);
                if (userDTO != null) {
                    session.setAttribute(ORIGINAL_LOGIN, userDTO.getLogin());
                }
            }

            return Route.USER;
        } else {
            return REDIRECT + Route.USERS;
        }
    }

    private UserDTO addUserDtoToModel(Model model, Long id) {
        Optional<UserDTO> userDTO = userService.get(id)
                .map(userMapper::userToUserDTOWithoutCollections);

        userDTO.ifPresent(dto -> model.addAttribute(USER, dto));
        return userDTO.orElse(null);
    }

    @PostMapping(USER)
    public String editUser(
            @Valid @ModelAttribute UserDTO userDTOFromModel,
            BindingResult bindingResult,
            @RequestParam(name = IMAGE, required = false) MultipartFile imageFile,
            @RequestParam(name = TEMP_IMAGE_ID, required = false) String tempImageId,
            @SessionAttribute(name = USER, required = false) UserDTO userDTOFromSession,
            @SessionAttribute(name = ORIGINAL_LOGIN, required = false) String originalLogin,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {
        if (userDTOFromSession == null) {
            return REDIRECT + Route.LOGIN;
        }
        if (!isUserPermitted(userDTOFromModel, userDTOFromSession)) {
            return REDIRECT + Route.PROFILE;
        }

        User userFromModel = userMapper.userDTOToUser(userDTOFromModel);
        Map<String, String[]> parameterMap = request.getParameterMap();

        try {
            if (parameterMap.containsKey(DELETE)) {
                return processDeletion(userFromModel, userDTOFromSession);
            } else if (parameterMap.containsKey(UPDATE)) {
                return processUpdate(
                        userDTOFromModel, userFromModel, bindingResult,
                        imageFile, tempImageId, redirectAttributes,
                        originalLogin, userDTOFromSession, request
                );
            } else {
//  todo              logger.warn("Unknown action in editUser");
                throw new AppException(UNKNOWN_COMMAND);
            }
        } catch (Exception e) {
//  todo          logger.error("Error in editUser: " + e.getMessage(), e);
            addLocalizedUnexpectedError(redirectAttributes);
            return REDIRECT + Route.INDEX;
        }
    }

    private String processDeletion(User userFromModel, UserDTO userDTOFromSession) {
        userAccountService.deleteExistingUser(userFromModel);
        if (userFromModel.getId().equals(userDTOFromSession.getId())) {
            return REDIRECT + Route.LOGOUT;
        } else {
            return REDIRECT + Route.USERS;
        }
    }

    private String processUpdate(UserDTO userDTOFromModel, User userFromModel, BindingResult bindingResult,
                                 MultipartFile imageFile, String tempImageId, RedirectAttributes redirectAttributes,
                                 String originalLogin, UserDTO userDTOFromSession, HttpServletRequest request) {
        UserDataProcessResult updateResult = userAccountService.processUserData(
                userDTOFromModel, bindingResult, imageFile, tempImageId, redirectAttributes, originalLogin);

        if (updateResult.hasFieldsErrors()) {
            redirectAttributes.addFlashAttribute(USER_DTO_FROM_MODEL, userDTOFromModel);
            return REDIRECT + Route.USER_ID + userDTOFromModel.getId();
        }

        userAccountService.updateExistingUser(userFromModel, imageFile, tempImageId, updateResult.isTempImagePresent(), updateResult.imageIsValid());
        return userFlowAndRedirect(request, userDTOFromSession, userFromModel);
    }

    private void addLocalizedUnexpectedError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
    }

    private boolean isUserPermitted(UserDTO userDTOFromModel, UserDTO userDTOFromSession) {
        if (!userDTOFromSession.getId().equals(userDTOFromModel.getId())) {
            Role userRole = userDTOFromSession.getRole();
            return acceptedRolesForEditing.contains(userRole);
        }
        return true;
    }

    private String userFlowAndRedirect(HttpServletRequest request, UserDTO userDTOFromSession, User userFromModel) {
        if (userDTOFromSession.getId().equals(userFromModel.getId())) {
            updateUserSession(request, userFromModel);
        }
        String source = (String) request.getSession().getAttribute(SOURCE);
        request.getSession().removeAttribute(SOURCE);
        return source != null ? REDIRECT + source : REDIRECT + Route.PROFILE;
    }

    private void updateUserSession(HttpServletRequest request, User userFromModel) {
        request.getSession()
                .setAttribute(
                        USER, userMapper.userToUserDTOWithoutPassword(userFromModel)
                );
    }
}
