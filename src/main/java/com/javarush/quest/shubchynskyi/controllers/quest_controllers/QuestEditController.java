package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.constant.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.constant.Key.*;


@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class QuestEditController {

    private final QuestService questService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ImageService imageService;
    private final QuestMapper questMapper;

    @GetMapping(QUEST_EDIT)
    public String showQuestForEdit(
            @RequestParam(ID) String id,
            Model model
    ) {
        Optional<Quest> quest = questService.get(id);
        if (quest.isPresent()) {
            model.addAttribute(
                    QUEST,
                    questMapper.questToQuestDTO(quest.get())
            );
            return QUEST_EDIT;
        } else {
            return REDIRECT + Route.QUEST_CREATE;
        }
    }

    @PostMapping(QUEST_EDIT)
    public String saveQuest(@RequestParam MultiValueMap<String, String> allParams, HttpServletRequest request) {
        String viewName;

        if (allParams.containsKey(QUEST_NAME)) {
            viewName = questEdit(allParams);
        } else if (allParams.containsKey(QUESTION_ID)) {
            viewName = questionEdit(allParams, request);
        } else {
            viewName = Route.QUESTS_LIST;
        }

        return viewName;
    }

    private String questEdit(MultiValueMap<String, String> allParams) {
        String questId = allParams.getFirst(ID);
        Optional<Quest> optionalQuest = questService.get(questId);

        if (optionalQuest.isPresent()) {
            Quest quest = optionalQuest.get();
            updateQuest(allParams, quest);
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questId);
        } else {
            return Route.QUESTS_LIST;  // fallback if quest not found
        }
    }

    private void updateQuest(MultiValueMap<String, String> allParams, Quest quest) {
        String newName = allParams.getFirst(QUEST_NAME);
        String newDescription = allParams.getFirst(QUEST_DESCRIPTION);

        if (newName != null) {
            quest.setName(newName);
        }
        if (newDescription != null) {
            quest.setDescription(newDescription);
        }

        questService.update(quest);
    }

    private String questionEdit(MultiValueMap<String, String> allParams, HttpServletRequest request) {
        String questionId = allParams.getFirst(QUESTION_ID);
        Optional<Question> optionalQuestion = questionService.get(questionId);

        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            updateQuestion(allParams, question, request);
            updateAnswers(allParams, question);
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, allParams.getFirst(ID))
                   + LABEL_URI_PATTERN + question.getId();
        } else {
            return Route.QUESTS_LIST;  // fallback if question not found
        }
    }

    private void updateQuestion(MultiValueMap<String, String> allParams, Question question, HttpServletRequest request) {
        try {
            imageService.uploadImage(request, question.getImage());
        } catch (IOException | ServletException e) {
            throw new AppException(IMAGE_UPLOAD_ERROR, e);
        }

        String newQuestionText = allParams.getFirst(QUESTION_TEXT);
        if (newQuestionText != null && !newQuestionText.equals(question.getText())) {
            question.setText(newQuestionText);
            questionService.update(question);
        }
    }

    private void updateAnswers(MultiValueMap<String, String> allParams, Question question) {
        for (Answer answer : question.getAnswers()) {
            String answerNewText = allParams.getFirst(ANSWER + answer.getId());
            if (answerNewText != null && !answerNewText.equals(answer.getText())) {
                answer.setText(answerNewText);
                answerService.update(answer);
            }
        }
    }

}
