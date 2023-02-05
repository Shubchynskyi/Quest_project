package com.javarush.quest.shubchynskyi.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Objects;

@UtilityClass
public class Jsp {

    public void forward(HttpServletRequest request, HttpServletResponse response, String target) throws ServletException, IOException {
        target = fixTarget(target);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(Key.WEB_INF_JSP_PATTERN.formatted(target));
        requestDispatcher.forward(request, response);
    }

    @SneakyThrows
    public void redirect(HttpServletResponse response, String target) {
        target = fixTarget(target);
        response.sendRedirect(target);
    }

    private String fixTarget(String target) {
        target = target.replace(Key.REGEX_SLASH_SIGN, Key.REGEX_EMPTY_STRING);
        return target;
    }

    public boolean isParameterPresent(HttpServletRequest request, String parameter) {
        HttpSession session = request.getSession();
        return Objects.nonNull(session.getAttribute(parameter));

    }
}
