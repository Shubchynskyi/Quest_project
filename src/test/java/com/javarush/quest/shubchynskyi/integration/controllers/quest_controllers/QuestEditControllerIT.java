package com.javarush.quest.shubchynskyi.integration.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.config.RoleConfig;
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
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.QUEST_NOT_FOUND_ERROR;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
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
    @Value("${valid.user.id}")
    private String validUserId;

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

    private Stream<String> supportedLanguagesProvider() {
        return Arrays.stream(supportedLanguages);
    }

    private Stream<Role> allowedRolesProvider() {
        return RoleConfig.ALLOWED_ROLES_FOR_QUEST_EDIT.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !RoleConfig.ALLOWED_ROLES_FOR_QUEST_EDIT.contains(role));
    }

    @Test
    void whenGetRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ID, validQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    @Transactional
    void whenQuestEditFormShownWithValidQuestId_ByAuthor_ShouldDisplayQuestForm() throws Exception {
        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ID, validQuestId)
                        .sessionAttr(USER, authorUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST))
                .andExpect(model().attributeExists(AUTHOR_ID))
                .andExpect(view().name(QUEST_EDIT));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenQuestEditFormAccessedByOtherUser_ThenShouldRedirectToProfile(Role notAllowedRole) throws Exception {
        UserDTO otherUser = UserDTO.builder()
                .id(Long.parseLong(validUserId) + 1)
                .role(notAllowedRole)
                .build();

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ID, validQuestId)
                        .sessionAttr(USER, otherUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void whenQuestEditFormAccessedByAllowedRole_ThenShouldDisplayQuestForm(Role allowedRole) throws Exception {
        UserDTO userWithAllowedRole = UserDTO.builder()
                .id(Long.parseLong(validUserId) + 1)
                .role(allowedRole)
                .build();

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ID, validQuestId)
                        .sessionAttr(USER, userWithAllowedRole))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST))
                .andExpect(model().attributeExists(AUTHOR_ID))
                .andExpect(view().name(QUEST_EDIT));
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("supportedLanguagesProvider")
    void whenRedirectedWithInvalidQuestId_ThenShouldShowProfileWithErrorMessage(String localeTag) throws Exception {
        Locale testLocale = Locale.forLanguageTag(localeTag);
        LocaleContextHolder.setLocale(testLocale);
        String expectedMessage = messageSource.getMessage(QUEST_NOT_FOUND_ERROR, null, testLocale);

        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ID, invalidQuestId)
                        .header(ACCEPT_LANGUAGE_HEADER, localeTag)
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, expectedMessage));
    }

    @Test
    void whenPostWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    @Transactional
    void whenQuestUpdatedWithValidData_ThenShouldRedirectToQuestEdit() throws Exception {
        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        String questName = generateUniqueString(UPDATED_QUEST_NAME, existingQuest.getName());
        String questDescription = generateUniqueString(UPDATED_QUEST_DESCRIPTION, existingQuest.getDescription());

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, questName)
                        .param(FIELD_DESCRIPTION, questDescription)
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId));

        Quest updatedQuest = getExistingQuest();
        assertEquals(questName, updatedQuest.getName());
        assertEquals(questDescription, updatedQuest.getDescription());
    }

    @Test
    @Transactional
    void whenQuestionUpdatedWithValidData_ThenShouldRedirectToUpdatedQuestion() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        String updatedQuestionText = generateUniqueString(UPDATED_QUESTION_TEXT, question.getText());

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUESTION)
                        .param(ID, question.getId().toString())
                        .param(QUEST_ID, existingQuest.getId().toString())
                        .param(QUESTION_TEXT, updatedQuestionText)
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId + LABEL_URL_FRAGMENT + question.getId()));

        Question updatedQuestion = questionService.get(question.getId()).orElseThrow();
        assertEquals(updatedQuestionText, updatedQuestion.getText());
    }

    @Test
    @Transactional
    void whenAnswersUpdatedWithValidData_ThenShouldRedirectToUpdatedAnswers() throws Exception {
        Quest existingQuest = getExistingQuest();
        Question question = getFirstQuestionFromQuest(existingQuest);
        Answer answer = question.getAnswers().stream().findFirst().orElseThrow();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        String updatedAnswerText = generateUniqueString(UPDATED_ANSWER_TEXT, answer.getText());

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUESTION)
                        .param(ID, question.getId().toString())
                        .param(QUEST_ID, existingQuest.getId().toString())
                        .param(ANSWER + answer.getId(), updatedAnswerText)
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + existingQuest.getId() + LABEL_URL_FRAGMENT + question.getId()));

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
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(multipart(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .file(imageFile)
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, existingQuest.getName())
                        .param(FIELD_DESCRIPTION, existingQuest.getDescription())
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId));

        mockMvc.perform(multipart(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .file(imageFile)
                        .param(ACTION_TYPE, QUESTION)
                        .param(ID, question.getId().toString())
                        .param(QUEST_ID, existingQuest.getId().toString())
                        .param(QUESTION_TEXT, question.getText())
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId + LABEL_URL_FRAGMENT + question.getId()));

        validateImages(imageBytes, existingQuest.getImage(), question.getImage());
    }

    @Test
    @Transactional
    void whenQuestUpdatedWithInvalidData_ThenShouldReturnValidationErrors() throws Exception {
        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, "")
                        .param(FIELD_DESCRIPTION, "")
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenUnauthorizedUserAttemptsToUpdateQuest_ThenShouldRedirectWithError(Role notAllowedRole) throws Exception {
        UserDTO unauthorizedUser = UserDTO.builder()
                .id(Long.parseLong(validUserId) + 1)
                .role(notAllowedRole)
                .build();

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, UPDATED_QUEST_NAME)
                        .param(FIELD_DESCRIPTION, UPDATED_QUEST_DESCRIPTION)
                        .param(AUTHOR_ID, validUserId)
                        .sessionAttr(USER, unauthorizedUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @Test
    @Transactional
    void whenInvalidImageUploadedForQuest_ThenShouldReturnImageError() throws Exception {
        MockMultipartFile invalidImageFile = new MockMultipartFile(
                IMAGE, "test.txt", "text/plain", "invalid content".getBytes());

        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(multipart(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .file(invalidImageFile)
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, existingQuest.getName())
                        .param(FIELD_DESCRIPTION, existingQuest.getDescription())
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId))
                .andExpect(flash().attributeExists(QUEST_IMAGE_ERROR));
    }

    @Test
    @Transactional
    void whenRequiredParametersAreMissing_ThenShouldReturnError() throws Exception {
        Quest existingQuest = getExistingQuest();
        UserDTO authorUser = createUserDTOFromQuestAuthor(existingQuest);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .sessionAttr(USER, authorUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(QUEST_EDIT_ID_URL + validQuestId))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @Test
    @Transactional
    void whenSessionTimesOutDuringUpdate_ThenShouldRedirectToLogin() throws Exception {
        Quest existingQuest = getExistingQuest();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, createUserDTOFromQuestAuthor(existingQuest));
        session.invalidate();

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_EDIT))
                        .param(ACTION_TYPE, QUEST)
                        .param(ID, validQuestId)
                        .param(FIELD_NAME, UPDATED_QUEST_NAME)
                        .param(FIELD_DESCRIPTION, UPDATED_QUEST_DESCRIPTION)
                        .param(AUTHOR_ID, existingQuest.getAuthor().getId().toString())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @NotNull
    private Quest getExistingQuest() {
        return questService.get(validQuestId).orElseThrow();
    }

    @NotNull
    private Question getFirstQuestionFromQuest(Quest existingQuest) {
        return existingQuest.getQuestions().stream().findFirst().orElseThrow();
    }

    @NotNull
    private UserDTO createUserDTOFromQuestAuthor(Quest quest) {
        return UserDTO.builder()
                .id(quest.getAuthor().getId())
                .login(quest.getAuthor().getLogin())
                .password(quest.getAuthor().getPassword())
                .role(quest.getAuthor().getRole())
                .quests(List.of())
                .build();
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