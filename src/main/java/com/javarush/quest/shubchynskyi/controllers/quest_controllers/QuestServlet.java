package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.config.JavaApplicationConfig;
import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
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

    private final QuestService questService = JavaApplicationConfig.getBean(QuestService.class);
    private final QuestionService questionService = JavaApplicationConfig.getBean(QuestionService.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Quest> questOptional = questService.get(request.getParameter(Key.ID));
        if (questOptional.isPresent()) {
            Quest quest = questOptional.get();
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap.containsKey(Key.QUESTION)) {
                setQuestionToRequest(request);
            } else {
                request.setAttribute(Key.START_QUESTION_ID, quest.getStartQuestionId());
                request.setAttribute(Key.QUEST_DESCRIPTION, quest.getDescription());
            }
            request.setAttribute(Key.ID, quest.getId());
            request.setAttribute(Key.QUEST_NAME, quest.getName());
            Jsp.forward(request, response, Go.QUEST);
        } else {
            Jsp.redirect(response, Go.QUESTS_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey(Key.GAME_STATE) && !request.getParameter(Key.GAME_STATE).equals(GameState.PLAY.name())) {
            Jsp.redirect(response, Go.QUESTS_LIST);
        } else {
            String questionId = request.getParameter(Key.QUESTION_ID);
            fillRequestAndRedirect(request, response, questionId);
        }
    }

    private void setQuestionToRequest(HttpServletRequest request) {
        String questionId = request.getParameter(Key.QUESTION);
        Optional<Question> questionOptional = questionService.get(questionId);
        questionOptional.ifPresent(question -> request.setAttribute(Key.QUESTION, question));
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
            String newUri = Key.NEXT_QUESTION_URI_PATTERN.formatted(Go.QUEST, Key.ID, questId, Key.QUESTION, question.getId());
            Jsp.redirect(response, newUri);
        } else {
            Jsp.redirect(response, Go.QUESTS_LIST);
        }
    }
}