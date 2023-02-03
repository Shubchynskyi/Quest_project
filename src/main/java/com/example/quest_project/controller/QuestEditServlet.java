package com.example.quest_project.controller;

import com.example.quest_project.config.Config;
import com.example.quest_project.entity.Answer;
import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.service.AnswerService;
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
@WebServlet(name = "QuestEditServlet", value = Go.QUEST_EDIT)
@MultipartConfig(fileSizeThreshold = 1 << 20)
public class QuestEditServlet extends HttpServlet {

    private final QuestService questService = QuestService.QUEST_SERVICE;
    private final QuestionService questionService = QuestionService.QUESTION_SERVICE;
    private final AnswerService answerService = AnswerService.ANSWER_SERVICE;
    private final ImageService imageService = ImageService.IMAGE_SERVICE;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO это пока не используется
        String questId = request.getParameter("id");
        if (questService.get(questId).isPresent()) {
            request.setAttribute("quest", questService.get(questId).get());
        }

        Jsp.forward(request, response, Key.QUEST_EDIT);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey("questName")) {
            if (questService.get(request.getParameter("id")).isPresent()) {
                Quest quest = questService.get(request.getParameter("id")).get();
                quest.setName(request.getParameter("questName"));
                quest.setDescription(request.getParameter("questDescription"));
                questService.update(quest);
                Jsp.redirect(response, Go.QUEST_EDIT+ "?id=" + request.getParameter("id"));
            }
        } else if (parameterMap.containsKey("questionId")) {
            String questionId = request.getParameter("questionId");
            if (questionService.get(questionId).isPresent()) {
                Question question = questionService.get(questionId).get();
                imageService.uploadImage(request, question.getImage());
                question.setText(request.getParameter("questionText"));
                questionService.update(question);
                for (Answer answer : question.getAnswers()) {
                    String answerText = request.getParameter(String.valueOf(answer.getId()));
                    answer.setText(answerText);
                    answerService.update(answer);
                }
                Jsp.redirect(response, Go.QUEST_EDIT + "?id=" + request.getParameter("id") + "#label-" + question.getId());
            }
        } else {
            Jsp.forward(request, response, Go.QUESTS_LIST);
        }



//todo quest-edit String pattern
        // отрефакторить - разбить на два метода - редактировать квест и вопрос
    }
}
