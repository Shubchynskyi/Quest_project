package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.localization.ViewErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import lombok.RequiredArgsConstructor;
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
                    return QUEST_EDIT;
                })
                .orElseGet(() -> {
                    String localizedMessage = ViewErrorLocalizer.getLocalizedMessage(QUEST_NOT_FOUND_ERROR);
                    redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
                    return REDIRECT + Route.QUEST_CREATE;
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
            viewName = Route.QUESTS_LIST;
        }

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
                    return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questId);
                })
                .orElse(Route.QUESTS_LIST);
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
        }
        if (newDescription != null) {
            quest.setDescription(newDescription);
        }

        imageService.uploadImage(imageFile, quest.getImage());

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
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, allParams.getFirst(ID))
                   + LABEL_URI_PATTERN + question.getId();
        }).orElse(Route.QUESTS_LIST);

    }

    private void updateQuestion(
            MultiValueMap<String, String> allParams,
            Question question,
            MultipartFile imageFile
    ) {
        imageService.uploadImage(imageFile, question.getImage());

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
