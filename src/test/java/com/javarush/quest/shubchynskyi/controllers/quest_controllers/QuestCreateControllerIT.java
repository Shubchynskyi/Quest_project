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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestCreateControllerIT {

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
    @Value("${valid.user.role}")
    private String validUserRole;

    private String validQuestText;
    private UserDTO userDTO;

    @BeforeAll
    void setUp() throws IOException {
        validQuestText = loadTextFromFile(validQuestTextPath);
        userDTO = new UserDTO();
        userDTO.setId(Long.valueOf(validUserId));
        userDTO.setRole(Role.valueOf(validUserRole));
        userDTO.setQuests(new ArrayList<>());
    }

    private String loadTextFromFile(String path) throws IOException {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Stream<Role> allowedRolesProvider() {
        return QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.stream();
    }

    private static Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.contains(role));
    }

    private static Stream<Arguments> provideParametersForMissingTest() {
        return Stream.of(
                Arguments.of(QUEST_NAME, HttpStatus.FOUND),
                Arguments.of(QUEST_TEXT, HttpStatus.BAD_REQUEST),
                Arguments.of(QUEST_DESCRIPTION, HttpStatus.FOUND)
        );
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void showCreateQuestPage_WithAllowedRoles_ShouldReturnCreateQuestView(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.CREATE_QUEST)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_QUEST));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void showCreateQuestPage_WithForbiddenRoles_ShouldRedirectToProfile(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);

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
        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST_DTO))
                .andExpect(view().name(CREATE_QUEST));
    }

    @Test
    @Transactional
    public void createQuest_SuccessAndNameAlreadyExists_ShouldReturnError() throws Exception {
        // Create the first quest and verify redirection
        Long questId = createQuestAndAssertRedirect(validQuestName, validQuestDescription, validQuestText, userDTO);

        // Verify the created quest details
        Quest createdQuest = questService.get(questId).orElseThrow();
        assertQuestDetails(createdQuest, validQuestName, validQuestDescription, validUserId);

        // Attempt to create a second quest with the same name and expect an error
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(TestConstants.NAME, validQuestName)
                        .param(TestConstants.DESCRIPTION, validQuestDescription)
                        .param(TestConstants.QUEST_TEXT, validQuestText)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(ERROR));
    }

    private Long createQuestAndAssertRedirect(String name, String description, String text, UserDTO userDTO) throws Exception {
        MvcResult result = mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(TestConstants.NAME, name)
                        .param(TestConstants.DESCRIPTION, description)
                        .param(TestConstants.QUEST_TEXT, text)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl, TestConstants.REDIRECTED_URL_DOES_NOT_MATCH_THE_EXPECTED_URL_PATTERN);

        assertTrue(redirectedUrl.startsWith(Route.QUEST_EDIT), TestConstants.REDIRECTED_URL_DOES_NOT_MATCH_THE_EXPECTED_URL_PATTERN);

        String questIdStr = redirectedUrl.substring(redirectedUrl.lastIndexOf('=') + 1);
        return Long.parseLong(questIdStr);
    }

    private void assertQuestDetails(Quest quest, String expectedName, String expectedDescription, String expectedUserId) {
        assertAll(
                () -> assertNotNull(quest, TestConstants.QUEST_SHOULD_NOT_BE_NULL),
                () -> assertEquals(expectedName, quest.getName(), TestConstants.QUEST_NAME_SHOULD_MATCH),
                () -> assertEquals(expectedDescription, quest.getDescription(), TestConstants.QUEST_DESCRIPTION_SHOULD_MATCH),
                () -> assertEquals(expectedUserId, String.valueOf(quest.getAuthor().getId()), TestConstants.AUTHOR_ID_SHOULD_MATCH)
        );
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
        return params;
    }

    @Test
    void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        userDTO.setRole(Role.valueOf(validUserRole));
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(QUEST_NAME, validQuestName)
                        .param(QUEST_DESCRIPTION, validQuestDescription)
                        .param(QUEST_TEXT, invalidQuestText)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void whenCreateQuestWithInvalidData_ThenReturnValidationErrors(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(QUEST_NAME, EMPTY_STRING)
                        .param(QUEST_DESCRIPTION, EMPTY_STRING)
                        .param(QUEST_TEXT, EMPTY_STRING)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(FIELD_ERRORS));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenUserLacksRole_ThenRedirectToProfileWithErrorMessage(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }
}