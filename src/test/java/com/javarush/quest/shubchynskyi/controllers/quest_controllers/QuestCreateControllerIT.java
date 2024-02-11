package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestCreateControllerIT {

    @Autowired
    MockMvc mockMvc;

    private static String validQuestText;
    private static final String VALID_QUEST_NAME = "Название квеста";
    private static final String VALID_QUEST_DESCRIPTION = "Описание квеста";
    private static final String VALID_USER_ID = "1"; //TODO вынести в настройки?
    private static final String INVALID_QUEST_TEXT = "Invalid text";

    @Value("${valid.quest.text.path}")
    private String validQuestTextPath;

    @BeforeAll
    void setUp() throws IOException {
        Path path = Paths.get(new ClassPathResource(validQuestTextPath).getURI());
        validQuestText = Files.readString(path);
    }

    // todo внести изменения в контроллер и проверять для каждой роли пользователя
    @Test
    public void showCreateQuestPage_WhenUserLoggedIn_ShouldReturnCreateQuestView() throws Exception {
        User user = new User();
        user.setRole(Role.USER);

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(Key.USER, user))
                .andExpect(status().isOk())
                .andExpect(view().name(Key.CREATE_QUEST));
    }

    @Test
    void showCreateQuestPage_WhenUserNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.CREATE_QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(Route.REDIRECT + Route.LOGIN))
                .andExpect(flash().attributeExists(Key.SOURCE));
    }

    @Test
    @Transactional
    public void createQuest_Success_ShouldRedirectToEditPageWithQuestId() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, VALID_QUEST_NAME)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(Route.QUEST_EDIT + "?id={id}"));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForMissingTest")
    void createQuest_WhenMissingParameters_ShouldReturnError(String missingParam) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(Key.QUEST_NAME, VALID_QUEST_NAME);
        params.add(Key.QUEST_TEXT, validQuestText);
        params.add(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION);
        params.add(Key.ID, VALID_USER_ID);

        params.remove(missingParam);

        mockMvc.perform(post(Route.CREATE_QUEST).params(params))
                .andExpect(status().isBadRequest());
    }

    static private Stream<String> provideParametersForMissingTest() {
        return Stream.of(Key.QUEST_NAME, Key.QUEST_TEXT, Key.QUEST_DESCRIPTION, Key.ID);
    }


    @ParameterizedTest
    @MethodSource("provideInvalidParameters")
    void createQuest_WhenInvalidData_ShouldRedirectAccordingly(String paramName, String paramValue, String expectedRedirectStart) throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, paramName.equals(Key.QUEST_NAME) ? paramValue : VALID_QUEST_NAME)
                        .param(Key.QUEST_TEXT, paramName.equals(Key.QUEST_TEXT) ? paramValue : validQuestText)
                        .param(Key.QUEST_DESCRIPTION, paramName.equals(Key.QUEST_DESCRIPTION) ? paramValue : VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, paramName.equals(Key.ID) ? paramValue : VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    String actualRedirect = result.getResponse().getRedirectedUrl();
                    Assertions.assertTrue(Objects.requireNonNull(actualRedirect).startsWith(expectedRedirectStart));
                });
    }

    // todo все строки вынести
    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of(Key.QUEST_NAME, "", Route.QUEST_EDIT + "?id="),
                Arguments.of(Key.QUEST_TEXT, "", Key.CREATE_QUEST),
                Arguments.of(Key.QUEST_DESCRIPTION, "", Route.QUEST_EDIT + "?id="),
                Arguments.of(Key.ID, "invalid", Key.CREATE_QUEST)
        );
    }

    @Test
    public void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, VALID_QUEST_NAME)
                        .param(Key.QUEST_TEXT, INVALID_QUEST_TEXT)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST))
                .andExpect(flash().attributeExists(Key.ERROR));
    }
}
