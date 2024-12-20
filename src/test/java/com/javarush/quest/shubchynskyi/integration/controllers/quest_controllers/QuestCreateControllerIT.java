package com.javarush.quest.shubchynskyi.integration.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.config.RoleConfig;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;
import static com.javarush.quest.shubchynskyi.constant.Route.QUEST_EDIT_ID;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class QuestCreateControllerIT {

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

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuestService questService;

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

    private Stream<Role> allowedRolesProvider() {
        return RoleConfig.ALLOWED_ROLES_FOR_QUEST_CREATE.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !RoleConfig.ALLOWED_ROLES_FOR_QUEST_CREATE.contains(role));
    }

    private static Stream<Arguments> provideParametersForMissingTest() {
        return Stream.of(
                Arguments.of(QUEST_NAME, CREATE_QUEST),
                Arguments.of(QUEST_TEXT, INDEX),
                Arguments.of(QUEST_DESCRIPTION, CREATE_QUEST)
        );
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void showCreateQuestPage_WithAllowedRoles_ShouldReturnCreateQuestView(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_QUEST));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void showCreateQuestPage_WithForbiddenRoles_ShouldRedirectToProfile(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }

    @Test
    void whenUserNotLoggedIn_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.CREATE_QUEST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(SOURCE, CREATE_QUEST));
    }

    @Test
    void showCreateQuestPage_WhenQuestDTOAbsent_AddsQuestDTOToModel() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(QUEST_DTO))
                .andExpect(view().name(CREATE_QUEST));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    public void createQuest_SuccessAndNameAlreadyExists_ShouldReturnError(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        Long createdQuestId = createQuestAndAssertRedirect(validQuestName, validQuestDescription, validQuestText, userDTO);

        Quest createdQuest = questService.get(createdQuestId).orElseThrow();
        assertQuestDetails(createdQuest, validQuestName, validQuestDescription, validUserId);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .param(FIELD_NAME, validQuestName)
                        .param(FIELD_DESCRIPTION, validQuestDescription)
                        .param(QUEST_TEXT, validQuestText)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(ERROR));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForMissingTest")
    void createQuest_WhenMissingParameters_ShouldReturnExpectedStatus(String missingParam, String expectedRedirect) throws Exception {
        MultiValueMap<String, String> params = buildValidParams();
        params.remove(missingParam);

        userDTO.setRole(Role.USER);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .params(params)
                        .sessionAttr(USER, userDTO))
                .andExpect(redirectedUrl(expectedRedirect));
    }

    private MultiValueMap<String, String> buildValidParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(QUEST_NAME, validQuestName);
        params.add(QUEST_DESCRIPTION, validQuestDescription);
        params.add(QUEST_TEXT, validQuestText);
        return params;
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.CREATE_QUEST))
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
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.CREATE_QUEST))
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
    void whenUserNotAllowedRole_ThenRedirectToProfileWithErrorMessage(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, YOU_DONT_HAVE_PERMISSIONS));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void createQuest_ShouldAddQuestToUserAndSession(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        Long createdQuestId = createQuestAndAssertRedirect(validQuestName, validQuestDescription, validQuestText, userDTO);

        assertTrue(userDTO.getQuests().stream()
                        .anyMatch(questDTO -> questDTO.getId().equals(createdQuestId)),
                ASSERT_QUEST_ADDED_TO_USER_LIST);

        MvcResult result = mockMvc.perform(get(
                        TestPathResolver.resolvePath(Route.QUEST_EDIT + QUERY_PARAM_ID + createdQuestId))
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO updatedUser = (UserDTO) Objects.requireNonNull(result.getRequest().getSession()).getAttribute(USER);

        assertNotNull(updatedUser, ASSERT_UPDATED_USER_NOT_NULL);
        assertTrue(updatedUser.getQuests().stream()
                        .anyMatch(questDTO -> questDTO.getId().equals(createdQuestId)),
                ASSERT_QUEST_ADDED_TO_USER_SESSION_LIST);
    }

    private Long createQuestAndAssertRedirect(String name, String description, String text, UserDTO userDTO) throws Exception {
        MvcResult result = mockMvc.perform(post(TestPathResolver.resolvePath(Route.CREATE_QUEST))
                        .param(FIELD_NAME, name)
                        .param(FIELD_DESCRIPTION, description)
                        .param(QUEST_TEXT, text)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(QUEST_EDIT_ID + WILDCARD))
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl, ERROR_REDIRECT_URL_MISMATCH);

        assertTrue(redirectedUrl.startsWith(Route.QUEST_EDIT), ERROR_REDIRECT_URL_MISMATCH);

        String questIdStr = redirectedUrl.substring(redirectedUrl.lastIndexOf('=') + 1);
        return Long.parseLong(questIdStr);
    }

    private void assertQuestDetails(Quest quest, String expectedName, String expectedDescription, String expectedUserId) {
        assertAll(
                () -> assertNotNull(quest, ASSERT_QUEST_NOT_NULL),
                () -> assertEquals(expectedName, quest.getName(), ASSERT_QUEST_NAME_MATCH),
                () -> assertEquals(expectedDescription, quest.getDescription(), ASSERT_QUEST_DESCRIPTION_MATCH),
                () -> assertEquals(expectedUserId, String.valueOf(quest.getAuthor().getId()), ASSERT_AUTHOR_ID_MATCH)
        );
    }

}