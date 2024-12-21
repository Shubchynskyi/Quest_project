package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestionDTO;
import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.mapper.QuestionMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestGameController {

    private final QuestService questService;
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    @GetMapping(QUEST)
    public String startQuest(
            @RequestParam(ID) String questId,
            HttpServletRequest request
    ) {
        return questService.get(questId)
                .map(quest -> {
                    log.info("Starting quest with ID: {}", questId);
                    return prepareQuestStart(request, quest);
                })
                .orElseGet(() -> {
                    log.warn("Quest not found with ID: {}", questId);
                    return REDIRECT + Route.QUESTS_LIST;
                });
    }

    @PostMapping(QUEST)
    public String nextStep(@RequestParam(name = GAME_STATE, required = false) String gameState,
                           @RequestParam(name = QUESTION_ID, required = false) String questionId,
                           HttpServletRequest request) {
        if (gameState != null && !gameState.equals(GameState.PLAY.toString())) {
            log.info("Game ended with state: {}", gameState);
            return REDIRECT + Route.QUESTS_LIST;
        } else {
            if (questionId != null) {
                return fillRequestAndRedirect(request, questionId);
            } else {
                log.warn("Question ID is missing, redirecting to quests list.");
                return REDIRECT + Route.QUESTS_LIST;
            }
        }
    }

    private String prepareQuestStart(HttpServletRequest request, Quest quest) {
        if (request.getParameterMap().containsKey(QUESTION)) {
            setQuestionToRequest(request);
        } else {
            request.setAttribute(START_QUESTION_ID, quest.getStartQuestionId());
            request.setAttribute(QUEST_DESCRIPTION, quest.getDescription());
            request.setAttribute(QUEST_IMAGE, quest.getImage());
        }
        request.setAttribute(ID, quest.getId());
        request.setAttribute(QUEST_NAME, quest.getName());
        log.info("Prepared quest start for quest ID: {}", quest.getId());
        return Route.QUEST;
    }

    private void setQuestionToRequest(HttpServletRequest request) {
        String questionId = request.getParameter(QUESTION);
        QuestionDTO questionDTO = questionService.get(questionId)
                .map(questionMapper::questionToQuestionDTO)
                .orElseThrow(() -> {
                    log.error("Question not found with ID: {}", questionId);
                    return new AppException(QUESTION_NOT_FOUND);
                });
        request.setAttribute(QUESTION, questionDTO);
    }

    private String fillRequestAndRedirect(HttpServletRequest request, String questionId) {
        return questionService.get(questionId)
                .map(question -> {
                    log.info("Filling request and redirecting for question ID: {}", questionId);
                    return prepareQuestionRedirect(request, question);
                })
                .orElseGet(() -> {
                    log.warn("Question not found with ID: {}", questionId);
                    return REDIRECT + Route.QUESTS_LIST;
                });
    }

    private String prepareQuestionRedirect(HttpServletRequest request, Question question) {
        String questId = request.getParameter(ID);
        String questName = request.getParameter(QUEST_NAME);
        QuestionDTO questionDTO = questionMapper.questionToQuestionDTO(question);
        request.setAttribute(QUESTION, questionDTO);
        request.setAttribute(ID, questId);
        request.setAttribute(QUEST_NAME, questName);
        String newUri = NEXT_QUESTION_URI_PATTERN.formatted(Route.QUEST, ID, questId, QUESTION, questionDTO.getId());
        log.info("Prepared redirect to the next question with ID: {}", question.getId());
        return REDIRECT + newUri;
    }
}