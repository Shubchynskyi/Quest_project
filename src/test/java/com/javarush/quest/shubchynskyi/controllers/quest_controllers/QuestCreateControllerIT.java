package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestCreateControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestService questService;

    @Value("${valid.quest.text.path}")
    private String validQuestTextPath;
    @Value("${valid.quest.name}")
    private String validQuestName;
    @Value("${valid.quest.description}")
    private String validQuestDescription;
    @Value("${valid.user.id}")
    private String validUserId;
    @Value("${invalid.quest.text}")
    private String invalidQuestText;

    private String validQuestText;

    @BeforeAll
    void setUp() throws IOException {
        validQuestText = loadTextFromFile(validQuestTextPath);
    }

    private static String loadTextFromFile(String path) throws IOException {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Stream<Role> allowedRolesProvider() {
        return QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.stream();
    }

    private static Stream<String> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.contains(role))
                .map(Enum::name);
    }

    private static Stream<Arguments> provideParametersForMissingTest() {
        return Stream.of(
                Arguments.of(QUEST_NAME, HttpStatus.FOUND),
                Arguments.of(QUEST_TEXT, HttpStatus.BAD_REQUEST),
                Arguments.of(QUEST_DESCRIPTION, HttpStatus.FOUND),
                Arguments.of(ID, HttpStatus.BAD_REQUEST)
        );
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void showCreateQuestPage_WithAllowedRoles_ShouldReturnCreateQuestView(Role allowedRole) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_QUEST));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void showCreateQuestPage_WithForbiddenRoles_ShouldRedirectToProfile(String roleName) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(Role.valueOf(roleName));

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }

    @Test
    void whenUserNotLoggedIn_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.CREATE_QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(SOURCE, CREATE_QUEST));
    }

    @Test
    void showCreateQuestPage_WhenQuestDTOAbsent_AddsQuestDTOToModel() throws Exception {
        UserDTO userDTO = new UserDTO();
        // todo role from config
        userDTO.setRole(QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.iterator().next());

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST_DTO))
                .andExpect(view().name(CREATE_QUEST));
    }

    @Test
    @Transactional
    public void createQuest_SuccessAndNameAlreadyExists_ShouldReturnError() throws Exception {
        Long questId = createQuestAndAssertRedirect(validQuestName, validQuestDescription, validQuestText, validUserId, Route.QUEST_EDIT);

        Quest createdQuest = questService.get(questId).orElseThrow();
        assertQuestDetails(createdQuest, validQuestName, validQuestDescription, validUserId);

        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(TestConstants.NAME, validQuestName)
                        .param(TestConstants.DESCRIPTION, validQuestDescription)
                        .param(QUEST_TEXT, validQuestText)
                        .param(ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(ERROR));
    }

    private Long createQuestAndAssertRedirect(String name, String description, String text, String userId, String expectedRedirect) throws Exception {
        MvcResult result = mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(TestConstants.NAME, name)
                        .param(TestConstants.DESCRIPTION, description)
                        .param(QUEST_TEXT, text)
                        .param(ID, userId))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        String expectedRedirectPattern = expectedRedirect + TestConstants.REDIRECT_URL_PATTERN;
        assertTrue(Objects.requireNonNull(redirectedUrl).matches(expectedRedirectPattern), TestConstants.REDIRECTED_URL_DOES_NOT_MATCH_THE_EXPECTED_URL_PATTERN);

        return Long.parseLong(redirectedUrl.substring(redirectedUrl.lastIndexOf('=') + 1));
    }

    private void assertQuestDetails(Quest quest, String expectedName, String expectedDescription, String expectedUserId) {
        assertNotNull(quest);
        assertEquals(expectedName, quest.getName());
        assertEquals(expectedDescription, quest.getDescription());
        assertEquals(expectedUserId, quest.getAuthor().getId().toString());
    }


    @ParameterizedTest
    @MethodSource("provideParametersForMissingTest")
    void createQuest_WhenMissingParameters_ShouldReturnExpectedStatus(String missingParam, HttpStatus expectedStatus) throws Exception {
        MultiValueMap<String, String> params = buildValidParams();
        params.remove(missingParam);

        mockMvc.perform(post(Route.CREATE_QUEST).params(params))
                .andExpect(status().is(expectedStatus.value()));
    }

    private MultiValueMap<String, String> buildValidParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(QUEST_NAME, validQuestName);
        params.add(QUEST_DESCRIPTION, validQuestDescription);
        params.add(QUEST_TEXT, validQuestText);
        params.add(ID, validUserId);
        return params;
    }

    @Test
    void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(QUEST_NAME, validQuestName)
                        .param(QUEST_DESCRIPTION, validQuestDescription)
                        .param(QUEST_TEXT, invalidQuestText)
                        .param(ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @Test
    void whenCreateQuestWithInvalidData_ThenReturnValidationErrors() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(QUEST_NAME, EMPTY_STRING)
                        .param(QUEST_DESCRIPTION, EMPTY_STRING)
                        .param(QUEST_TEXT, EMPTY_STRING)
                        .param(ID, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenUserLacksRole_ThenRedirectToProfileWithErrorMessage(String roleName) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(Role.valueOf(roleName));

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }
}
