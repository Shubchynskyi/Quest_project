package com.javarush.quest.shubchynskyi.exception.handlers;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.javarush.quest.shubchynskyi.constant.Key.ERROR;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.ID_NOT_FOUND_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;

@ControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler({NumberFormatException.class, MethodArgumentTypeMismatchException.class})
    public String handleInvalidFormat(Exception ex, RedirectAttributes redirectAttributes) {
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(ID_NOT_FOUND_ERROR);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
        return REDIRECT + INDEX;
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
        String localizedMessage = ErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR);
        redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
        return REDIRECT + INDEX;
    }
}