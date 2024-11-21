package com.javarush.quest.shubchynskyi.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.dto.QuestionDTO;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.mapper.QuestMapper;
import com.javarush.quest.shubchynskyi.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QuestEditController {

    private final QuestService questService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ImageService imageService;
    private final QuestMapper questMapper;
    private final ValidationService validationService;

    protected static final List<Role> ALLOWED_ROLES_FOR_QUEST_EDIT =
            List.of(Role.MODERATOR, Role.ADMIN);

    @GetMapping(QUEST_EDIT)
    public String showQuestForEdit(
            @RequestParam(ID) String id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) String source
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        Optional<Quest> questOptional = questService.get(id);

        if (questOptional.isEmpty()) {
            String localizedMessage = ErrorLocalizer.getLocalizedMessage(QUEST_NOT_FOUND_ERROR);
            redirectAttributes.addFlashAttribute(ERROR, localizedMessage);
            log.warn("Quest not found with ID: {}", id);
            return REDIRECT + (source != null ? source : Route.QUESTS_LIST);
        }

        if (Objects.nonNull(currentUser)) {
            Quest quest = questOptional.get();
            Long authorId = quest.getAuthor().getId();

            if (validationService.checkUserAccessDenied(session, ALLOWED_ROLES_FOR_QUEST_EDIT, redirectAttributes)
                    && !Objects.equals(currentUser.getId(), authorId)) {
                log.warn("Access denied to quest edit: insufficient permissions. Quest ID: {}. User ID: {}", id, currentUser.getId());
                return REDIRECT + (source != null ? source : Route.QUESTS_LIST);
            }

            if (source != null) {
                model.addAttribute(SOURCE, source);
            }
            model.addAttribute(QUEST, questMapper.questToQuestDTO(quest));
            log.info("Displaying quest edit page for quest ID: {}", id);
            return QUEST_EDIT;
        } else {
            log.warn("User is not logged in, redirecting to quests list page.");
            return REDIRECT + Route.LOGIN;
        }
    }

    @PostMapping(QUEST_EDIT)
    public String saveQuest(
            @RequestParam(ACTION_TYPE) String actionType,
            @Valid @ModelAttribute(QUEST) QuestDTO questDTO,
            BindingResult questBindingResult,
            @Valid @ModelAttribute(QUESTION_DTO) QuestionDTO questionDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam MultiValueMap<String, String> allParams,
            @RequestParam(name = IMAGE, required = false) MultipartFile imageFile,
            @RequestParam(required = false) String source
    ) {
        UserDTO currentUser = (UserDTO) session.getAttribute(USER);
        if (Objects.nonNull(currentUser)) {

            if (validationService.checkUserAccessDenied(session, ALLOWED_ROLES_FOR_QUEST_EDIT, redirectAttributes)) {
                log.warn("Access denied to quest edit: insufficient permissions. User ID: {}", currentUser.getId());
                return REDIRECT + (source != null ? source : Route.QUESTS_LIST);
            }

            if (source != null) {
                redirectAttributes.addAttribute(SOURCE, source);
            }

            String viewName = (source != null ? source : Route.QUESTS_LIST);

            if (actionType.equals(QUEST)) {
                viewName = questEdit(imageFile, redirectAttributes, questDTO, questBindingResult, currentUser, source);
            } else if (actionType.equals(QUESTION)) {
                viewName = questionEdit(allParams, imageFile, questionDTO, redirectAttributes, source);
            }

            log.info("Saving quest with parameters: {}", allParams);
            return viewName;
        } else {
            log.warn("User is not logged in, redirecting to quests list page.");
            return REDIRECT + Route.LOGIN;
        }
    }

    private String questEdit(
            MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            QuestDTO questDTO,
            BindingResult questBindingResult,
            UserDTO currentUser,
            String source
    ) {

        if (!imageFile.isEmpty()) {
            if (imageService.isValid(imageFile)) {
                if (imageFile.getSize() > imageService.getMaxFileSize()) {
                    redirectAttributes.addFlashAttribute(QUEST_IMAGE_ERROR,
                            FILE_IS_TOO_LARGE + imageService.getMaxFileSize());
                } else {
                    imageService.uploadFromMultipartFile(imageFile, questDTO.getImage(), false);
                }
            } else {
                redirectAttributes.addFlashAttribute(QUEST_IMAGE_ERROR, IMAGE_FILE_IS_INCORRECT);
            }
        }

        boolean fieldErrors = validationService.processFieldErrors(questBindingResult, redirectAttributes);
        if (fieldErrors) {
            redirectAttributes.addFlashAttribute(QUEST_DTO, questDTO);
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questDTO.getId());
        }

        return questService.get(questDTO.getId())
                .map(quest -> {
                    quest.setName(questDTO.getName());
                    quest.setDescription(questDTO.getDescription());
                    questService.update(quest);

                    currentUser.getQuests().stream()
                            .filter(userQuest -> userQuest.getId().equals(questDTO.getId()))
                            .findFirst()
                            .ifPresent(userQuest -> {
                                userQuest.setName(questDTO.getName());
                                userQuest.setDescription(questDTO.getDescription());
                            });
                    redirectAttributes.addFlashAttribute(USER, currentUser);
                    log.info("Quest updated successfully: {}", questDTO.getId());
                    return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questDTO.getId());
                })
                .orElseGet(() -> {
                    log.warn("Quest not found for updating with ID: {}", questDTO.getId());
                    return REDIRECT + (source != null ? source : Route.QUESTS_LIST);
                });
    }


    private String questionEdit(
            MultiValueMap<String, String> allParams,
            MultipartFile imageFile,
            QuestionDTO questionDTO,
            RedirectAttributes redirectAttributes,
            String source
    ) {
        Optional<Question> optionalQuestion = questionService.get(questionDTO.getId());

        return optionalQuestion.map(question -> {
            updateQuestion(allParams, question, imageFile, redirectAttributes);
            updateAnswers(allParams, question);
            log.info("Question updated successfully: {}", questionDTO.getId());
            return REDIRECT + ID_URI_PATTERN.formatted(Route.QUEST_EDIT, questionDTO.getQuestId())
                    + LABEL_URI_PATTERN + question.getId();
        }).orElseGet(() -> {
            log.warn("Question not found with ID: {}", questionDTO.getId());
            return REDIRECT + (source != null ? source : Route.QUESTS_LIST);
        });
    }

    private void updateQuestion(
            MultiValueMap<String, String> allParams,
            Question question,
            MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) {
        if (!imageFile.isEmpty()) {
            Map<Long, String> questionImageErrors = new HashMap<>();

            if (imageService.isValid(imageFile)) {
                if (imageFile.getSize() > imageService.getMaxFileSize()) {
                    questionImageErrors.put(question.getId(),
                            FILE_IS_TOO_LARGE + imageService.getMaxFileSize());
                } else {
                    imageService.uploadFromMultipartFile(imageFile, question.getImage(), false);
                }
            } else {
                questionImageErrors.put(question.getId(), IMAGE_FILE_IS_INCORRECT);
            }

            if (!questionImageErrors.isEmpty()) {
                redirectAttributes.addFlashAttribute(QUESTION_IMAGE_ERRORS, questionImageErrors);
            }
        }

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