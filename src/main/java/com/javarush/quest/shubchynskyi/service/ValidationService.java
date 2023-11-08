package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final MessageSource messageSource;

    public void processFieldErrors(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
            Map<String, String> errors = new HashMap<>();
            Locale locale = LocaleContextHolder.getLocale();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String localizedErrorMessage = messageSource.getMessage(error, locale);
                errors.put(error.getField(), localizedErrorMessage);
            }
            redirectAttributes.addFlashAttribute(FIELD_ERRORS, errors);
    }

    public boolean checkUserAccessDenied(HttpSession session, List<Role> validRoles, RedirectAttributes redirectAttributes) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (currentUser == null || validRoles.stream().noneMatch(role -> role.equals(currentUser.getRole()))) {
            String localizedMessage = messageSource.getMessage(YOU_DONT_HAVE_PERMISSIONS, null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            return true;
        }
        return false;
    }


}
