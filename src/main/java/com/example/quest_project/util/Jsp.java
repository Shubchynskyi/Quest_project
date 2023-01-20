package com.example.quest_project.util;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class Jsp {
    public void forward(HttpServletRequest request, HttpServletResponse response, String target) throws ServletException, IOException {
        target = fixTarget(target);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/%s.jsp".formatted(target)); // указали куда перенаправить
        requestDispatcher.forward(request, response); // отправили
    }

    @SneakyThrows
    public void redirect(HttpServletResponse response, String target) {
        target = fixTarget(target);
        response.sendRedirect(target);
    }

    private String fixTarget(String target) {
        target = target.replace("/", "");
        return target;
    }
}
