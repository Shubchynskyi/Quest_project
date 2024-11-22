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

    // TODO отрефакторить
    public boolean checkUserAccessDenied(HttpSession session, List<Role> validRoles, RedirectAttributes redirectAttributes, Long userPermitId) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if(userPermitId == null && currentUser != null && (validRoles.contains(currentUser.getRole()))) {
            return false;
        }
        if (currentUser != null && (validRoles.contains(currentUser.getRole()) || currentUser.getId().equals(userPermitId))) {
            return false;
        } else {
            String localizedMessage = messageSource.getMessage(YOU_DONT_HAVE_PERMISSIONS, null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Access denied for user: {}", currentUser != null ? currentUser.getLogin() : "unknown");
            return true;
        }
    }

    public boolean checkUserAccessDenied(HttpSession session, List<Role> validRoles, RedirectAttributes redirectAttributes) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null || validRoles.stream().noneMatch(role -> role.equals(currentUser.getRole()))) {
            String localizedMessage = messageSource.getMessage(YOU_DONT_HAVE_PERMISSIONS, null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Access denied for user: {}", currentUser != null ? currentUser.getLogin() : "unknown");
            return true;
        }
        return false;
    }
}