package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class QuestGameController {

    private final QuestService questService;
    private final QuestionService questionService;

    @GetMapping("quest")
    public String startQuest(
            HttpServletRequest request
    ) {
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
            return "quest";
        } else {
            return "redirect:quests-list";
        }
    }

    @PostMapping("quest")
    public String nextStep(HttpServletRequest request,
                           HttpServletResponse response) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey(Key.GAME_STATE)
            && !request.getParameter(Key.GAME_STATE).equals(GameState.PLAY.name())) {
            return "redirect:/quests-list";
        } else {
            String questionId = request.getParameter(Key.QUESTION_ID);
            return fillRequestAndRedirect(request, response, questionId);
        }
    }

    private void setQuestionToRequest(HttpServletRequest request) {
        String questionId = request.getParameter(Key.QUESTION);
        Optional<Question> questionOptional = questionService.get(questionId);
        questionOptional.ifPresent(question -> request.setAttribute(Key.QUESTION, question));
    }

    private String fillRequestAndRedirect(HttpServletRequest request, HttpServletResponse response, String questionId) {
        Optional<Question> questionOptional = questionService.get(questionId);
        if (questionOptional.isPresent()) {
            String questId = request.getParameter(Key.ID);
            String questName = request.getParameter(Key.QUEST_NAME);
            Question question = questionOptional.get();
            request.setAttribute(Key.QUESTION, question);
            request.setAttribute(Key.ID, questId);
            request.setAttribute(Key.QUEST_NAME, questName);
            String newUri = Key.NEXT_QUESTION_URI_PATTERN.formatted(Go.QUEST, Key.ID, questId, Key.QUESTION, question.getId());
            return "redirect:" + newUri;
        } else {
            return "redirect:/quests-list";
        }
    }
}