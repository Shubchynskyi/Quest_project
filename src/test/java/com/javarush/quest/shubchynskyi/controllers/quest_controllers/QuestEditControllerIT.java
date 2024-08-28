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
    @Value("${app.images.test-image.name}")
    private String testImageName;
    @Value("${app.images.test-image.content-type}")
    private String testImageContentType;
    @Value("${app.localization.supported-languages}")
    private String[] supportedLanguages;
    @Value("${app.valid-quest-id}")
    private String validQuestId;
    @Value("${app.invalid-quest-id}")
    private String invalidQuestId;

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

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, invalidQuestId)
                        .header(TestConstants.ACCEPT_LANGUAGE, localeTag))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.CREATE_QUEST))
                .andExpect(flash().attribute(ERROR, expectedMessage));
    }

    @Test
    void postWithoutQuestAndQuestionParams_WithValidQuestId_ShouldShowQuestsList() throws Exception {
        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(view().name(QUESTS_LIST));
    }

    @Test
    @Transactional
    void updateQuest_WithValidData_ShouldRedirectToQuestEdit() throws Exception {
        Quest existingQuest = getExistingQuest();

        String questName = generateUniqueString(TestConstants.UPDATED_QUEST_NAME, existingQuest.getName());
        String questDescription = generateUniqueString(TestConstants.UPDATED_QUEST_DESCRIPTION, existingQuest.getDescription());

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUEST_NAME, questName)
                        .param(QUEST_DESCRIPTION, questDescription))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId));

        Quest updatedQuest = getExistingQuest();
        assertEquals(questName, updatedQuest.getName());
        assertEquals(questDescription, updatedQuest.getDescription());
    }

    @Test
    @Transactional
    void updateQuestion_WithValidData_ShouldRedirectToUpdatedQuestion() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);

        String updatedQuestionText = generateUniqueString(TestConstants.UPDATED_QUESTION_TEXT, question.getText());

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString())
                        .param(QUESTION_TEXT, updatedQuestionText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + question.getId()));

        Question updatedQuestion = questionService.get(question.getId()).orElseThrow();
        assertEquals(updatedQuestionText, updatedQuestion.getText());
    }

    @Test
    @Transactional
    void updateAnswers_WithValidData_ShouldRedirectToUpdatedAnswers() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        Answer answer = question.getAnswers().stream().findFirst().orElseThrow();

        String updatedAnswerText = generateUniqueString(TestConstants.UPDATED_ANSWER_TEXT, answer.getText());

        mockMvc.perform(MockMvcRequestBuilders.post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString())
                        .param(ANSWER + answer.getId(), updatedAnswerText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + question.getId()));

        Answer updatedAnswer = answerService.get(answer.getId()).orElseThrow();
        assertEquals(updatedAnswerText, updatedAnswer.getText());
    }

    @Test
    @Transactional
    void uploadImages_ForQuestAndQuestion_ShouldMatchTestImage() throws Exception {
        byte[] imageBytes = loadImageBytes(testImageName);
        MockMultipartFile imageFile = new MockMultipartFile(IMAGE, testImageName, testImageContentType, imageBytes);

        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(QUEST_NAME, existingQuest.getName())
                        .param(ID, validQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId));

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_PATH + validQuestId + LABEL_FRAGMENT + question.getId()));

        validateImages(imageBytes, existingQuest.getImage(), question.getImage());
    }

    @NotNull
    private Quest getExistingQuest() {
        return questService.get(validQuestId).orElseThrow();
    }

    @NotNull
    private Question getFirstQuestionFromQuest(Quest existingQuest) {
        return existingQuest.getQuestions().stream().findFirst().orElseThrow();
    }

    private byte[] loadImageBytes(String imageName) throws Exception {
        Path imagePath = Path.of(imagesDirectory, imageName);
        return Files.readAllBytes(imagePath);
    }

    private void validateImages(byte[] originalImageBytes, String questImage, String questionImage) throws Exception {
        String fileExtension = FilenameUtils.getExtension(testImageName);
        Path questImagePath = Paths.get(imagesDirectory, questImage + "." + fileExtension);
        Path questionImagePath = Paths.get(imagesDirectory, questionImage + "." + fileExtension);

        byte[] savedQuestImage = Files.readAllBytes(questImagePath);
        byte[] savedQuestionImage = Files.readAllBytes(questionImagePath);

        assertArrayEquals(originalImageBytes, savedQuestImage, TestConstants.THE_QUEST_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE);
        assertArrayEquals(originalImageBytes, savedQuestionImage, TestConstants.THE_QUESTION_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE);

        Files.deleteIfExists(questImagePath);
        Files.deleteIfExists(questionImagePath);
    }

    private String generateUniqueString(String base, String existingValue) {
        StringBuilder stringBuilder = new StringBuilder(base);
        while (stringBuilder.toString().equals(existingValue)) {
            stringBuilder.append(TestConstants.APPEND_LETTER);
        }
        return stringBuilder.toString();
    }

    private Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }
}
