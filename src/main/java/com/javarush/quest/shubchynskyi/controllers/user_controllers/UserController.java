package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.ImageService;
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

import java.util.*;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserMapper userMapper;
    private final ValidationService validationService;

    private final List<Role> acceptedRoles = List.of(Role.ADMIN, Role.MODERATOR);

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
        if (validationService.checkUserAccessDenied(session, acceptedRoles, redirectAttributes)) {
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
                userDTO = getUserDTOWithPassword(userFromSession);
                model.addAttribute(USER, userDTO);
                session.setAttribute("originalLogin", userDTO.getLogin());
            } else {
                userDTO = addUserDtoToModel(model, id);
                if (userDTO != null) {
                    session.setAttribute("originalLogin", userDTO.getLogin());
                }
            }

//            // Проверяем, существует ли у пользователя изображение
//            boolean userImageExists = userDTO != null && userDTO. != null && !user.getImagePath().isEmpty();
//
//            // Добавляем эту информацию в модель
//            model.addAttribute("userImageExists", userImageExists);

            return Route.USER;
        } else {
            return REDIRECT + Route.USERS;
        }
    }

    private UserDTO addUserDtoToModel(Model model, Long id) {
        Optional<User> optionalUser = userService.get(id);
        if (optionalUser.isPresent()) {
            UserDTO userDTO = userMapper.userToUserDTOWithoutCollections(optionalUser.get());
            model.addAttribute(USER, userDTO);
            return userDTO; // Теперь метод возвращает UserDTO
        }
        return null;
    }

    @PostMapping(USER)
    public String editUser(
            @Valid @ModelAttribute UserDTO userDTOFromModel,
            BindingResult bindingResult,
            @RequestParam(name = IMAGE, required = false) MultipartFile imageFile,
            @RequestParam(name = TEMP_IMAGE_ID, required = false) String tempImageId,

            @SessionAttribute(name = USER, required = false) UserDTO userDTOFromSession,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = "originalLogin", required = false) String originalLogin,
//            @RequestParam(IMAGE) MultipartFile imageFile,
            HttpServletRequest request
    ) {

        //если пользователя нет в сессии, то редирект на логин
        if (userDTOFromSession == null) {
            return REDIRECT + Route.LOGIN;
        }

        // проверяет есть ли у пользователя права на редактирование
        if (!isUserPermitted(userDTOFromModel, userDTOFromSession)) {
            return REDIRECT + Route.PROFILE;
        }


        try {

            // проверяю есть ли ошибки и вношу их локализованные сообщения в редирект атрибут
            boolean hasFieldsErrors = processFieldErrors(bindingResult, redirectAttributes);
            boolean imageIsValid = imageService.isValid(imageFile);
            boolean isTempImagePresent = !tempImageId.isEmpty(); // есть ли временное изображение

            // если изображение валидное, но большое
            if (imageIsValid && imageFile.getSize() > MAX_FILE_SIZE) {
                addLocalizedMaxSizeError(redirectAttributes);
                hasFieldsErrors = true;
                imageIsValid = false;
                if (!tempImageId.isEmpty()) {
                    tempImageId = "";
                }
                // если не валидное, но при этом файл изображения не пустой
            } else if ((!imageIsValid && !imageFile.isEmpty())) {
                addLocalizedIncorrectImageError(redirectAttributes);
                hasFieldsErrors = true;
            }

            if (!userDTOFromModel.getLogin().equals(originalLogin)) {
                hasFieldsErrors = isLoginExist(userDTOFromModel, redirectAttributes, hasFieldsErrors);
            }

            // если все таки есть ошибки и при этом изображение было валидным, то его надо сохранить как временное
            if (hasFieldsErrors && imageIsValid && !isTempImagePresent) {
                String tempImageUUID = UUID.randomUUID().toString();
                String fullTempImageName = imageService.uploadFromMultipartFile(imageFile, tempImageUUID, true);
                redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, fullTempImageName);
                // если есть ошибки, но временное присутствует (по сути - не менялось в текущем редактировании
            } else if (hasFieldsErrors && isTempImagePresent) {
                redirectAttributes.addFlashAttribute(TEMP_IMAGE_ID, tempImageId);
            }

            // если есть ошибки, то добавляем текущего пользователя и отправляем в get
            if (hasFieldsErrors) {
                redirectAttributes.addFlashAttribute(USER_DTO_FROM_MODEL, userDTOFromModel);
                return REDIRECT + Route.USER_ID + userDTOFromModel.getId();
            }

            // TODO вынести весь код ниже в транзакцию
            // получаем юзера из сессии
            userDTOFromSession = getUserDTOWithPassword(userDTOFromSession);
            User userFromModel = userMapper.userDTOToUser(userDTOFromModel);


            // Здесь в зависимости от команды внутри выполняем действие и возвращаем правильный редирект
            String redirectUrl = performUserActionAndResolveRedirect(userDTOFromSession, redirectAttributes, request, userFromModel);

            if (redirectUrl != null) {
                return redirectUrl;
            }

            // TODO обработать изображение нужно в правильный момент
//            загружаем изображение
            if (!isTempImagePresent) {
                imageService.uploadFromMultipartFile(imageFile, userFromModel.getImage(), false);
            } else {
                imageService.uploadFromExistingFile(tempImageId, userFromModel.getImage());
            }

            // возвращает правильный редирект
            return handleAdminFlow(request, userDTOFromSession, userFromModel);
        } catch (AppException e) {
            // TODO log
            addLocalizedUnexpectedError(redirectAttributes);
            return REDIRECT + Route.USERS;
        }
    }

    private void addLocalizedUnexpectedError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR);
        redirectAttributes.addFlashAttribute(IMAGING_ERROR, localizedMessage);
    }

    private void addLocalizedIncorrectImageError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(IMAGE_FILE_IS_INCORRECT);
        redirectAttributes.addFlashAttribute(IMAGING_ERROR, localizedMessage);
    }

    private void addLocalizedMaxSizeError(RedirectAttributes redirectAttributes) {
        String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(FILE_IS_TOO_LARGE);
        redirectAttributes.addFlashAttribute(
                IMAGING_ERROR, localizedMessage
                        + " " + (MAX_FILE_SIZE / KB_TO_MB / KB_TO_MB)
                        + " " + MB);
    }

    private boolean processFieldErrors(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        boolean hasFieldsErrors = bindingResult.hasErrors();
        if (hasFieldsErrors) {
            validationService.processFieldErrors(bindingResult, redirectAttributes);
        }
        return hasFieldsErrors;
    }

    private boolean isLoginExist(UserDTO userDTOFromModel, RedirectAttributes redirectAttributes, boolean hasErrors) {
        if (userService.isLoginExist(userDTOFromModel.getLogin())) {
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            hasErrors = true;
        }
        return hasErrors;
    }

    private UserDTO getUserDTOWithPassword(UserDTO userDTOFromSession) {
        return userService.get(userDTOFromSession.getId())
                .map(userMapper::userToUserDTOWithoutCollections)
                .orElseThrow();
    }

    private boolean isUserPermitted(UserDTO userDTOFromModel, UserDTO userDTOFromSession) {
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
            String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
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
