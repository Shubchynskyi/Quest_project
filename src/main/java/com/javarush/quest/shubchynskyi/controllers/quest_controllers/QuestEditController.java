package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_NOT_FOUND_ERROR;

@Slf4j
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
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        return questService.get(id)
                .map(quest -> {
                    model.addAttribute(QUEST, questMapper.questToQuestDTO(quest));
                    log.info("Displaying quest edit page for quest ID: {}", id);
                    return QUEST_EDIT;
                })
                .orElseGet(() -> {
                    String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_NOT_FOUND_ERROR);
                    redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
                    log.warn("Quest not found with ID: {}", id);
                    return REDIRECT + Route.CREATE_QUEST;
                });
    }

    @PostMapping(QUEST_EDIT)
    public String saveQuest(
            @RequestParam MultiValueMap<String, String> allParams,
            @RequestParam(name = IMAGE, required = false) MultipartFile imageFile
    ) {
        String viewName;

        if (allParams.containsKey(QUEST_NAME)) {
            viewName = questEdit(allParams, imageFile);
        } else if (allParams.containsKey(QUESTION_ID)) {
            viewName = questionEdit(allParams, imageFile);
        } else {
            viewName = QUESTS_LIST; // todo убрал /, было из Route, проверить
        }

        log.info("Saving quest with parameters: {}", allParams);
        return viewName;
    }

    private String questEdit(
            MultiValueMap<String, String> allParams,
            MultipartFile imageFile
    ) {
        String questId = allParams.getFirst(ID);

        return questService.get(questId)
                .map(quest -> {
                    updateQuest(allParams, quest, imageFile);
                    log.info("Quest updated successfully: {}", questId);
                    return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questId);
                })
                .orElseGet(() -> {
                    log.warn("Quest not found for updating with ID: {}", questId);
                    return Route.QUESTS_LIST;
                });
    }

    private void updateQuest(
            MultiValueMap<String, String> allParams,
            Quest quest,
            MultipartFile imageFile
    ) {
        String newName = allParams.getFirst(QUEST_NAME);
        String newDescription = allParams.getFirst(QUEST_DESCRIPTION);

        if (newName != null) {
            quest.setName(newName);
            log.info("Updated quest name to: {}", newName);
        }
        if (newDescription != null) {
            quest.setDescription(newDescription);
            log.info("Updated quest description. Quest name: {}", quest.getName());
        }

        imageService.uploadFromMultipartFile(imageFile, quest.getImage(), false);

        questService.update(quest);
    }

    private String questionEdit(
            MultiValueMap<String, String> allParams,
            MultipartFile imageFile
    ) {
        String questionId = allParams.getFirst(QUESTION_ID);
        Optional<Question> optionalQuestion = questionService.get(questionId);

        return optionalQuestion.map(question -> {
            updateQuestion(allParams, question, imageFile);
            updateAnswers(allParams, question);
            log.info("Question updated successfully: {}", questionId);
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, allParams.getFirst(ID))
                    + LABEL_URI_PATTERN + question.getId();
        }).orElseGet(() -> {
            log.warn("Question not found with ID: {}", questionId);
            return Route.QUESTS_LIST;
        });
    }

    private void updateQuestion(
            MultiValueMap<String, String> allParams,
            Question question,
            MultipartFile imageFile
    ) {
        imageService.uploadFromMultipartFile(imageFile, question.getImage(), false);

        String newQuestionText = allParams.getFirst(QUESTION_TEXT);
        if (newQuestionText != null && !newQuestionText.equals(question.getText())) {
            question.setText(newQuestionText);
            questionService.update(question);
            log.info("Updated question text for question ID: {}", question.getId());
        }
    }

    private void updateAnswers(MultiValueMap<String, String> allParams, Question question) {
        for (Answer answer : question.getAnswers()) {
            String answerNewText = allParams.getFirst(ANSWER + answer.getId());
            if (answerNewText != null && !answerNewText.equals(answer.getText())) {
                answer.setText(answerNewText);
                answerService.update(answer);
                log.info("Updated answer text for answer ID: {}", answer.getId());
            }
        }
    }
}