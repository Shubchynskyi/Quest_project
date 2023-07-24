package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.dto.QuestionDTO;
import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.mapper.QuestionMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class QuestGameController {

    private final QuestService questService;
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    @GetMapping("quest")
    public String startQuest(
            @RequestParam(Key.ID) String questId,
            HttpServletRequest request
    ) {
        return questService.get(questId)
                .map(quest -> prepareQuestStart(request, quest))
                .orElse("redirect:quests-list");
    }

    @PostMapping("quest")
    public String nextStep(@RequestParam(name = Key.GAME_STATE, required = false) String gameState,
                           @RequestParam(name = Key.QUESTION_ID, required = false) String questionId,
                           HttpServletRequest request) {
        if (gameState != null && !gameState.equals(GameState.PLAY.name())) {
            return "redirect:/quests-list";
        } else {
            if (questionId != null) {
                return fillRequestAndRedirect(request, questionId);
            } else {
                return "redirect:/quests-list";
            }
        }
    }

    private String prepareQuestStart(HttpServletRequest request, Quest quest) {
        if (request.getParameterMap().containsKey(Key.QUESTION)) {
            setQuestionToRequest(request);
        } else {
            request.setAttribute(Key.START_QUESTION_ID, quest.getStartQuestionId());
            request.setAttribute(Key.QUEST_DESCRIPTION, quest.getDescription());
        }
        request.setAttribute(Key.ID, quest.getId());
        request.setAttribute(Key.QUEST_NAME, quest.getName());
        return "quest";
    }

    private void setQuestionToRequest(HttpServletRequest request) {
        String questionId = request.getParameter(Key.QUESTION);
        QuestionDTO questionDTO = questionService.get(questionId)
                .map(questionMapper::questionToQuestionDTO)
                .orElseThrow();
        request.setAttribute(Key.QUESTION, questionDTO);
    }

    private String fillRequestAndRedirect(HttpServletRequest request, String questionId) {
        return questionService.get(questionId)
                .map(question -> prepareQuestionRedirect(request, question))
                .orElse("redirect:/quests-list");
    }

    private String prepareQuestionRedirect(HttpServletRequest request, Question question) {
        String questId = request.getParameter(Key.ID);
        String questName = request.getParameter(Key.QUEST_NAME);
        QuestionDTO questionDTO = questionMapper.questionToQuestionDTO(question);
        request.setAttribute(Key.QUESTION, questionDTO);
        request.setAttribute(Key.ID, questId);
        request.setAttribute(Key.QUEST_NAME, questName);
        String newUri = Key.NEXT_QUESTION_URI_PATTERN.formatted(Go.QUEST, Key.ID, questId, Key.QUESTION, questionDTO.getId());
        return "redirect:" + newUri;
    }
}