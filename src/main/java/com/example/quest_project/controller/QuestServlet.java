package com.example.quest_project.controller;

import com.example.quest_project.entity.GameState;
import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.service.ImageService;
import com.example.quest_project.service.QuestService;
import com.example.quest_project.service.QuestionService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@WebServlet(name = "QuestServlet", value = Go.QUEST)
public class QuestServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;
    QuestionService questionService = QuestionService.QUESTION_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Quest> questOptional = questService.get(request.getParameter(Key.ID));
        if (questOptional.isPresent()) {
            Quest quest = questOptional.get();
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap.containsKey(Key.QUESTION)) {
                String questionId = request.getParameter(Key.QUESTION);
                Optional<Question> questionOptional = questionService.get(questionId);
                questionOptional.ifPresent(question -> request.setAttribute(Key.QUESTION, question));
            } else {
                request.setAttribute(Key.START_QUESTION_ID, quest.getStartQuestionId());
                request.setAttribute(Key.QUEST_DESCRIPTION, quest.getDescription());
            }
            request.setAttribute(Key.ID, quest.getId());
            request.setAttribute(Key.QUEST_NAME, quest.getName());
            Jsp.forward(request, response, Key.QUEST);
        } else {
            Jsp.redirect(response, Key.QUESTS_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey(Key.GAME_STATE) && !request.getParameter(Key.GAME_STATE).equals(GameState.PLAY.name())) {
            Jsp.redirect(response, Key.QUESTS_LIST);
        } else {
            String questionId = request.getParameter(Key.QUESTION_ID);
            fillRequestAndRedirect(request, response, questionId);
        }
    }

    private void fillRequestAndRedirect(HttpServletRequest request, HttpServletResponse response, String questionId) {
        Optional<Question> questionOptional = questionService.get(questionId);
        if (questionOptional.isPresent()) {
            String questId = request.getParameter(Key.ID);
            String questName = request.getParameter(Key.QUEST_NAME);
            Question question = questionOptional.get();
            request.setAttribute(Key.QUESTION, question);
            request.setAttribute(Key.ID, questId);
            request.setAttribute(Key.QUEST_NAME, questName);
            String newUri = Key.URI_PATTERN.formatted(Key.QUEST, Key.ID, questId, Key.QUESTION, question.getId());
            Jsp.redirect(response, newUri);
        } else {
            Jsp.redirect(response, Key.QUESTS_LIST);
        }
    }
}