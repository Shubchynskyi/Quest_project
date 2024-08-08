package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.TestConstants.LABEL_FRAGMENT;
import static com.javarush.quest.shubchynskyi.TestConstants.QUEST_EDIT_PATH;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_NOT_FOUND_ERROR;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestEditControllerIT {

    @Autowired
    private QuestService questService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MockMvc mockMvc;

    @Value("${app.images-directory}")
    private String imagesDirectory;
    @Value("${app.valid-quest-id}")
    private String validQuestId;
    @Value("${app.invalid-quest-id}")
    private String invalidQuestId;
    @Value("${app.images.test-image.name}")
    private String testImageName;
    @Value("${app.images.test-image.content-type}")
    private String testImageContentType;
    @Value("${app.localization.supported-languages}")
    private String[] supportedLanguages;

    @Test
    void showQuestEditForm_WithValidQuestId_ShouldDisplayQuestForm() throws Exception {
        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST))
                .andExpect(view().name(QUEST_EDIT));
    }

    @ParameterizedTest
    @MethodSource("supportedLanguagesProvider")
    void redirect_WithInvalidQuestId_ShouldShowCreateQuestWithErrorMessage(String localeTag) throws Exception {
        Locale testLocale = Locale.forLanguageTag(localeTag);
        LocaleContextHolder.setLocale(testLocale);
        String expectedMessage = messageSource.getMessage(QUEST_NOT_FOUND_ERROR, null, testLocale);
        System.err.println(expectedMessage);

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, invalidQuestId)
                        .header(TestConstants.ACCEPT_LANGUAGE, localeTag))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.CREATE_QUEST))
                .andExpect(flash().attribute(ERROR, expectedMessage));
    }

    Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }

    @Test
    void postWithoutQuestAndQuestionParams_WithValidQuestId_ShouldShowQuestsList() throws Exception {
        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUESTS_LIST));
    }

    @Test
    @Transactional
    void updateQuest_WithValidData_ShouldRedirectToQuestEdit() throws Exception {
        Quest existingQuest = getExistingQuest();

        String baseQuestName = "Updated Quest Name";
        String baseQuestDescription = "Updated Quest Description";

        StringBuilder questName = new StringBuilder(baseQuestName);
        StringBuilder questDescription = new StringBuilder(baseQuestDescription);
        while (questName.toString().equals(existingQuest.getName()) || questDescription.toString().equals(existingQuest.getDescription())) {
            questName.append("a");
            questDescription.append("a");
        }

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUEST_NAME, questName.toString())
                        .param(QUEST_DESCRIPTION, questDescription.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId));

        Quest updatedQuest = getExistingQuest();
        assertEquals(questName.toString(), updatedQuest.getName());
        assertEquals(questDescription.toString(), updatedQuest.getDescription());
    }

    @Test
    @Transactional
    void updateQuestion_WithValidData_ShouldRedirectToUpdatedQuestion() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        Long questionId = question.getId();

        String originalQuestionText = question.getText();
        String updatedQuestionText = "Updated Question Text";
        if (originalQuestionText.equals(updatedQuestionText)) {
            updatedQuestionText += "a";
        }

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, questionId.toString())
                        .param(QUESTION_TEXT, updatedQuestionText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + questionId));

        Question updatedQuestion = questionService.get(questionId).orElseThrow();
        assertEquals(updatedQuestionText, updatedQuestion.getText());
    }

    @Test
    @Transactional
    void updateAnswers_WithValidData_ShouldRedirectToUpdatedAnswers() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        Long questionId = question.getId();

        Answer answer = question.getAnswers().stream().findFirst().orElseThrow();
        long answerId = answer.getId();
        String originalAnswerText = answer.getText();

        StringBuilder updatedAnswerText = new StringBuilder("Updated Answer Text");
        while (updatedAnswerText.toString().equals(originalAnswerText)) {
            updatedAnswerText.append("a");
        }

        mockMvc.perform(MockMvcRequestBuilders.post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, questionId.toString())
                        .param(ANSWER + answerId, updatedAnswerText.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + questionId));

        Answer updatedAnswer = answerService.get(answerId).orElseThrow();
        assertEquals(updatedAnswerText.toString(), updatedAnswer.getText());
    }

    @Test
    @Transactional
    void uploadImages_ForQuestAndQuestion_ShouldMatchTestImage() throws Exception {
        Path imagePath = Path.of(imagesDirectory, testImageName);
        byte[] imageBytes = Files.readAllBytes(imagePath);

        MockMultipartFile imageFile = new MockMultipartFile(IMAGE, testImageName, testImageContentType, imageBytes);

        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        String questionId = question.getId().toString();

        mockMvc.perform(multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(QUEST_NAME, existingQuest.getName())
                        .param(ID, validQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId));

        mockMvc.perform(multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + questionId));

        Quest updatedQuest = getExistingQuest();
        Question updatedQuestion = questionService.get(questionId).orElseThrow();

        String fileExtension = FilenameUtils.getExtension(testImageName);
        Path questImagePath = Paths.get(imagesDirectory, updatedQuest.getImage() + "." + fileExtension);
        Path questionImagePath = Paths.get(imagesDirectory, updatedQuestion.getImage() + "." + fileExtension);

        byte[] savedQuestImage = Files.readAllBytes(questImagePath);
        byte[] savedQuestionImage = Files.readAllBytes(questionImagePath);

        assertArrayEquals(imageBytes, savedQuestImage, "The quest image does not match the test image.");
        assertArrayEquals(imageBytes, savedQuestionImage, "The question image does not match the test image.");

        Files.deleteIfExists(questImagePath);
        Files.deleteIfExists(questionImagePath);
    }

    @NotNull
    private Quest getExistingQuest() {
        return questService.get(validQuestId).orElseThrow();
    }

    @NotNull
    private Question getFirstQuestionFromQuest(Quest existingQuest) {
        return existingQuest.getQuestions().stream().findFirst().orElseThrow();
    }
}