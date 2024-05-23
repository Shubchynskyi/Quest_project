package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Assumptions;
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
import java.util.*;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.QUEST_DTO;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestCreateControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    QuestService questService;
    public static final String EMPTY_STREAM_MARKER = "NONE";
    private static String validQuestText;

    @Value("${valid.quest.text.path}")
    private String validQuestTextPath;
    @Value("${valid.quest.name}")
    private String validQuestName;
    @Value("${valid.quest.description}")
    private String validQuestDescription;
    @Value("${valid.quest.userId}")
    private String validUserId;
    @Value("${invalid.quest.text}")
    private String invalidQuestText;

    @BeforeAll
    void setUp() throws IOException {
        try (InputStream inputStream = new ClassPathResource(validQuestTextPath).getInputStream()) {
            validQuestText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Stream<Role> allowedRolesProvider() {
        return QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.stream();
    }

    static Stream<String> notAllowedRolesProvider() {
        Set<Role> allRoles = EnumSet.allOf(Role.class);
        allRoles.removeAll(QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE);

        if (allRoles.isEmpty()) {
            return Stream.of(EMPTY_STREAM_MARKER);
        } else {
            return allRoles.stream().map(Enum::name);
        }
    }

    private static Stream<String> provideInvalidParameters() {
        return Stream.of(Key.QUEST_NAME, Key.QUEST_TEXT, Key.QUEST_DESCRIPTION, Key.ID);
    }

    private static Stream<Arguments> provideParametersForMissingTest() {
        return Stream.of(
                Arguments.of(Key.QUEST_NAME, HttpStatus.FOUND.value()),
                Arguments.of(Key.QUEST_TEXT, HttpStatus.BAD_REQUEST.value()),
                Arguments.of(Key.QUEST_DESCRIPTION, HttpStatus.FOUND.value()),
                Arguments.of(Key.ID, HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void showCreateQuestPage_WithAllowedRoles_ShouldReturnCreateQuestView(Role allowedRole) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(Key.USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name(Key.CREATE_QUEST));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void showCreateQuestPage_WithForbiddenRoles_ShouldRedirect(String roleName) throws Exception {
        Assumptions.assumeFalse(EMPTY_STREAM_MARKER.equals(roleName), TestConstants.NO_ROLES_TO_TEST);

        UserDTO userDTO = new UserDTO();
        userDTO.setRole(Role.valueOf(roleName));

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(Key.USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(Key.ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }

    @Test
    void showCreateQuestPage_WhenUserNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.CREATE_QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(Route.REDIRECT + Route.LOGIN))
                .andExpect(flash().attribute(Key.SOURCE, Key.CREATE_QUEST));
    }

    @Test
    public void showCreateQuestPage_WhenUserNotAuthenticated_ShouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get(Route.CREATE_QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(Key.SOURCE, Key.CREATE_QUEST));
    }

    @Test
    public void showCreateQuestPage_WhenQuestDTOAbsent_AddsQuestDTOToModel() throws Exception {
        Assumptions.assumeTrue(allowedRolesProvider().findAny().isPresent(), "No allowed roles available");

        UserDTO userDTO = new UserDTO();
        Role allowedRole = allowedRolesProvider().findAny().orElse(null);
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(Key.USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST_DTO))
                .andExpect(view().name(Key.CREATE_QUEST));
    }

    @Test
    @Transactional
    public void createQuest_SuccessAndNameAlreadyExists_ShouldReturnError() throws Exception {
        MvcResult result = mockMvc.perform(post(Route.CREATE_QUEST)
                        .param("name", validQuestName)
                        .param("description", validQuestDescription)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        String expectedRedirectPattern = Route.QUEST_EDIT + "\\?id=\\d+";
        assertTrue(Objects.requireNonNull(redirectedUrl).matches(expectedRedirectPattern), "Redirected URL does not match the expected URL pattern");

        String questIdStr = redirectedUrl.substring(redirectedUrl.lastIndexOf('=') + 1);
        Long questId = Long.parseLong(questIdStr);

        Quest createdQuest = questService.get(questId).orElseThrow();
        assertNotNull(createdQuest);
        assertEquals(validQuestName, createdQuest.getName());
        assertEquals(validQuestDescription, createdQuest.getDescription());
        assertEquals(validUserId, createdQuest.getAuthor().getId().toString());

        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param("name", validQuestName)
                        .param("description", validQuestDescription)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST))
                .andExpect(flash().attributeExists(Key.ERROR));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForMissingTest")
    void createQuest_WhenMissingParameters_ShouldActAccordingly(String missingParam, int expectedStatus) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(Key.QUEST_NAME, validQuestName);
        params.add(Key.QUEST_DESCRIPTION, validQuestDescription);
        params.add(Key.QUEST_TEXT, validQuestText);
        params.add(Key.ID, validUserId);

        params.remove(missingParam);

        mockMvc.perform(post(Route.CREATE_QUEST).params(params))
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParameters")
    void createQuest_WhenInvalidData_ShouldRedirectAccordingly(String paramName) throws Exception {
        String paramValue = TestConstants.EMPTY_STRING;

        Map<String, String> params = new HashMap<>();
        params.put(Key.QUEST_NAME, validQuestName);
        params.put(Key.QUEST_TEXT, validQuestText);
        params.put(Key.QUEST_DESCRIPTION, validQuestDescription);
        params.put(Key.ID, validUserId);

        params.put(paramName, paramValue);

        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, params.get(Key.QUEST_NAME))
                        .param(Key.QUEST_TEXT, params.get(Key.QUEST_TEXT))
                        .param(Key.QUEST_DESCRIPTION, params.get(Key.QUEST_DESCRIPTION))
                        .param(Key.ID, params.get(Key.ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST));
    }

    @Test
    public void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param("name", validQuestName)
                        .param("description", validQuestDescription)
                        .param(Key.QUEST_TEXT, invalidQuestText)
                        .param(Key.ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST))
                .andExpect(flash().attributeExists(Key.ERROR));
    }

    @Test
    public void createQuest_WhenValidationFails_ShouldRedirectWithValidationErrors() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param("name", "")
                        .param("description", "")
                        .param(Key.QUEST_TEXT, "")
                        .param(Key.ID, ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST))
                .andExpect(flash().attributeExists("fieldErrors"));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void showCreateQuestPage_WhenUserLacksRole_ShouldRedirectWithErrorMessage(String roleName) throws Exception {
        Assumptions.assumeFalse(EMPTY_STREAM_MARKER.equals(roleName), "No roles to test.");

        UserDTO userDTO = new UserDTO();
        userDTO.setRole(Role.valueOf(roleName));

        mockMvc.perform(get(Route.CREATE_QUEST).sessionAttr(Key.USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(Key.ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }

}
