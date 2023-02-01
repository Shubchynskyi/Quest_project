package com.example.quest_project.controller;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.service.ImageService;
import com.example.quest_project.service.QuestService;
import com.example.quest_project.service.QuestionService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

//TODO редирект на страницу со списком квестом
@WebServlet(name = "QuestEditServlet", value = Go.QUESTS_EDIT)
@MultipartConfig(fileSizeThreshold = 1 << 20)
public class QuestEditServlet extends HttpServlet {

    QuestionService questService = QuestionService.QUESTION_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Quest quest = (Quest) request.getAttribute("quest");
        Collection<Question> questions = quest.getQuestions();
        Question question = questions.stream().findAny().orElse(null);
        request.setAttribute("questions", questions);
        request.setAttribute("question", question);

        Jsp.forward(request, response, Key.QUEST_EDIT);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO не работает, изображения не загружаются
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (var var : parameterMap.entrySet()) {
            if(var.getKey().contains("id")){
                System.out.println(var.getKey());
                String[] value = var.getValue();
                String id = value[0];
                Optional<Question> questionOptional = questService.get(Long.valueOf(id));
                if (questionOptional.isPresent()) {
                    Question question = questionOptional.get();
                    imageService.uploadImage(request, question.getImage());
                }
            }

        }
//        imageService.uploadImage(request, user.getImage()); // загружаем аватар
        response.sendRedirect(Key.QUESTS_LIST);

    }
}
