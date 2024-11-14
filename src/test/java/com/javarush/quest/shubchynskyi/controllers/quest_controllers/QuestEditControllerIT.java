package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.service.AnswerService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.QuestionService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.TestConstants.*;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_NOT_FOUND_ERROR;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestEditControllerIT {

    @Value("${app.directories.images}")
    private String imagesDirectory;
    @Value("${app.images.test-image.name}")
    private String testImageName;
    @Value("${app.images.test-image.content-type}")
    private String testImageContentType;
    @Value("${app.localization.supported-languages}")
    private String[] supportedLanguages;
    @Value("${valid.quest.id}")
    private String validQuestId;
    @Value("${invalid.quest.id}")
    private String invalidQuestId;

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

    private UserDTO userDTO;

    private Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }

    private Stream<Role> allowedRolesProvider() {
        return QuestEditController.ALLOWED_ROLES_FOR_QUEST_EDIT.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !QuestEditController.ALLOWED_ROLES_FOR_QUEST_EDIT.contains(role));
    }

    @BeforeAll
    public void setup() {
        userDTO = new UserDTO();
    }

    @Test
    void whenGetRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, validQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    @Transactional
    void whenQuestEditFormShownWithValidQuestId_ByAuthor_ShouldDisplayQuestForm() throws Exception {
        Quest existingQuest = getExistingQuest();

        UserDTO authorUser = UserDTO.builder()
                .id(existingQuest.getAuthor().getId())
                .login(existingQuest.getAuthor().getLogin())
                .password(existingQuest.getAuthor().getPassword())
                .role(existingQuest.getAuthor().getRole())
                .build();

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .sessionAttr(USER, authorUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST))
                .andExpect(view().name(QUEST_EDIT));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenQuestEditFormAccessedByOtherUser_ThenShouldRedirectToQuestsList(Role notAllowedRole) throws Exception {
        userDTO.setId(Long.valueOf(validQuestId + 1));
        userDTO.setRole(notAllowedRole);

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void whenQuestEditFormAccessedByAdmin_ThenShouldDisplayQuestForm(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST))
                .andExpect(view().name(QUEST_EDIT));
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("supportedLanguagesProvider")
    void whenRedirectedWithInvalidQuestId_ThenShouldShowCreateQuestWithErrorMessage(String localeTag) throws Exception {
        Locale testLocale = Locale.forLanguageTag(localeTag);
        LocaleContextHolder.setLocale(testLocale);
        String expectedMessage = messageSource.getMessage(QUEST_NOT_FOUND_ERROR, null, testLocale);

        Quest existingQuest = getExistingQuest();
        UserDTO user = UserDTO.builder()
                .id(existingQuest.getAuthor().getId())
                .login(existingQuest.getAuthor().getLogin())
                .password(existingQuest.getAuthor().getPassword())
                .role(existingQuest.getAuthor().getRole())
                .build();

        mockMvc.perform(get(Route.QUEST_EDIT)
                        .param(ID, invalidQuestId)
                        .header(ACCEPT_LANGUAGE_HEADER, localeTag)
                        .sessionAttr(USER, user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.CREATE_QUEST))
                .andExpect(flash().attribute(ERROR, expectedMessage));
    }

    @Test
    void whenPostWithoutQuestAndQuestionParams_ThenShouldShowQuestsList() throws Exception {
        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(view().name(QUESTS_LIST));
    }

    @Test
    @Transactional
    void whenQuestUpdatedWithValidData_ThenShouldRedirectToQuestEdit() throws Exception {
        Quest existingQuest = getExistingQuest();

        String questName = generateUniqueString(UPDATED_QUEST_NAME, existingQuest.getName());
        String questDescription = generateUniqueString(UPDATED_QUEST_DESCRIPTION, existingQuest.getDescription());

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUEST_NAME, questName)
                        .param(QUEST_DESCRIPTION, questDescription))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_URL + validQuestId));

        Quest updatedQuest = getExistingQuest();
        assertEquals(questName, updatedQuest.getName());
        assertEquals(questDescription, updatedQuest.getDescription());
    }

    @Test
    @Transactional
    void whenQuestionUpdatedWithValidData_ThenShouldRedirectToUpdatedQuestion() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);

        String updatedQuestionText = generateUniqueString(UPDATED_QUESTION_TEXT, question.getText());

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString())
                        .param(QUESTION_TEXT, updatedQuestionText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_URL + validQuestId + LABEL_URL_FRAGMENT + question.getId()));

        Question updatedQuestion = questionService.get(question.getId()).orElseThrow();
        assertEquals(updatedQuestionText, updatedQuestion.getText());
    }

    @Test
    @Transactional
    void whenAnswersUpdatedWithValidData_ThenShouldRedirectToUpdatedAnswers() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        Answer answer = question.getAnswers().stream().findFirst().orElseThrow();

        String updatedAnswerText = generateUniqueString(UPDATED_ANSWER_TEXT, answer.getText());

        mockMvc.perform(post(Route.QUEST_EDIT)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString())
                        .param(ANSWER + answer.getId(), updatedAnswerText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_URL + validQuestId + LABEL_URL_FRAGMENT + question.getId()));

        Answer updatedAnswer = answerService.get(answer.getId()).orElseThrow();
        assertEquals(updatedAnswerText, updatedAnswer.getText());
    }

    @Test
    @Transactional
    void whenImagesUploadedForQuestAndQuestion_ThenShouldMatchTestImage() throws Exception {
        byte[] imageBytes = loadImageBytes(testImageName);
        MockMultipartFile imageFile = new MockMultipartFile(IMAGE, testImageName, testImageContentType, imageBytes);

        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);

        mockMvc.perform(multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(QUEST_NAME, existingQuest.getName())
                        .param(ID, validQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_URL + validQuestId));

        mockMvc.perform(multipart(Route.QUEST_EDIT)
                        .file(imageFile)
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, question.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_URL + validQuestId + LABEL_URL_FRAGMENT + question.getId()));

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
        Path questImagePath = Path.of(imagesDirectory, questImage + "." + fileExtension);
        Path questionImagePath = Path.of(imagesDirectory, questionImage + "." + fileExtension);

        byte[] savedQuestImage = Files.readAllBytes(questImagePath);
        byte[] savedQuestionImage = Files.readAllBytes(questionImagePath);

        assertArrayEquals(originalImageBytes, savedQuestImage, ERROR_QUEST_IMAGE_MISMATCH);
        assertArrayEquals(originalImageBytes, savedQuestionImage, ERROR_QUESTION_IMAGE_MISMATCH);

        Files.deleteIfExists(questImagePath);
        Files.deleteIfExists(questionImagePath);
    }

    private String generateUniqueString(String base, String existingValue) {
        StringBuilder stringBuilder = new StringBuilder(base);
        while (stringBuilder.toString().equals(existingValue)) {
            stringBuilder.append("q");
        }
        return stringBuilder.toString();
    }

}