package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final MessageSource messageSource;

    public boolean processFieldErrors(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        boolean hasFieldsErrors = bindingResult.hasErrors();
        if (hasFieldsErrors) {
            Map<String, String> errors = new HashMap<>();
            Locale locale = LocaleContextHolder.getLocale();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String localizedErrorMessage = messageSource.getMessage(error, locale);
                errors.put(error.getField(), localizedErrorMessage);
            }
            redirectAttributes.addFlashAttribute(FIELD_ERRORS, errors);
            log.warn("Validation errors found: {}", errors);
        }
        return hasFieldsErrors;
    }

    public boolean checkUserAccessDenied(HttpSession session, List<Role> validRoles, RedirectAttributes redirectAttributes, Long questAuthorId) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);

        if (isAccessAllowed(currentUser, validRoles, questAuthorId)) {
            return false;
        }

        denyAccess(currentUser, redirectAttributes);
        return true;
    }

    public boolean checkUserAccessDenied(HttpSession session, List<Role> validRoles, RedirectAttributes redirectAttributes) {
        return checkUserAccessDenied(session, validRoles, redirectAttributes, null);
    }

    private boolean isAccessAllowed(UserDTO currentUser, List<Role> validRoles, Long questAuthorId) {
        if (currentUser == null) {
            return false;
        }

        if (questAuthorId == null && validRoles.contains(currentUser.getRole())) {
            return true;
        }

        return validRoles.contains(currentUser.getRole()) || currentUser.getId().equals(questAuthorId);
    }

    private void denyAccess(UserDTO currentUser, RedirectAttributes redirectAttributes) {
        String localizedMessage = messageSource.getMessage(YOU_DONT_HAVE_PERMISSIONS, null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
        log.warn("Access denied for user: {}", currentUser != null ? currentUser.getLogin() : "unknown");
    }

}