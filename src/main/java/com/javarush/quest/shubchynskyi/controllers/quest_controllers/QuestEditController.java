package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;


@MultipartConfig(fileSizeThreshold = 1 << 20)
@Controller
@RequiredArgsConstructor
public class QuestEditController {

    private final QuestService questService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ImageService imageService;
    private final QuestMapper questMapper;

    @GetMapping("quest-edit")
    public String showQuestForEdit(
            @RequestParam("id") String id,
            Model model
    ) {
        Optional<Quest> quest = questService.get(id);
        if (quest.isPresent()) {
            model.addAttribute(
                    Key.QUEST,
                    questMapper.questToQuestDTO(quest.get())
            );
            return "quest-edit";
        } else {
            return "redirect:quest-create";
        }
    }

    @PostMapping("quest-edit")
    public void saveQuest(
            @RequestParam MultiValueMap<String, String> allParams,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        if (allParams.containsKey(Key.QUEST_NAME)) {
            questEdit(request, response);
        } else if (allParams.containsKey(Key.QUESTION_ID)) {
            questionEdit(request, response);
        } else {
            Jsp.forward(request, response, Go.QUESTS_LIST);
        }
    }

    private void questEdit(HttpServletRequest request, HttpServletResponse response) {
        if (questService.get(request.getParameter(Key.ID)).isPresent()) {
            Quest quest = questService.get(request.getParameter(Key.ID)).get();
            String newName = request.getParameter(Key.QUEST_NAME);
            quest.setName(newName);
            String newDescription = request.getParameter(Key.QUEST_DESCRIPTION);
            quest.setDescription(newDescription);
            questService.update(quest);
            Jsp.redirect(response, Key.ID_URI_PATTERN.formatted(Go.QUEST_EDIT, request.getParameter(Key.ID)));
        }
    }

    private void questionEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String questionId = request.getParameter(Key.QUESTION_ID);
        if (questionService.get(questionId).isPresent()) {
            Question question = questionService.get(questionId).get();
            imageService.uploadImage(request, question.getImage());
            String newQuestionText = request.getParameter(Key.QUESTION_TEXT);
            if (!newQuestionText.equals(question.getText())) {
                question.setText(newQuestionText);
                questionService.update(question);
            }
            for (Answer answer : question.getAnswers()) {
                String answerNewText = request.getParameter(Key.ANSWER + answer.getId());
                if (!answerNewText.equals(answer.getText())) {
                    answer.setText(answerNewText);
                    answerService.update(answer);
                }
            }
            Jsp.redirect(response,
                    Key.ID_URI_PATTERN.formatted(Go.QUEST_EDIT, request.getParameter(Key.ID))
                    + Key.LABEL_URI_PATTERN + question.getId());
        }
    }
}
