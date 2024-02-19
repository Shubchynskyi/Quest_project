package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.*;
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
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
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
        Path path = Paths.get(new ClassPathResource(validQuestTextPath).getURI());
        validQuestText = Files.readString(path);
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

    private static Stream<Role> allowedRolesProvider() {
        return QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE.stream();
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

    static Stream<String> notAllowedRolesProvider() {
        Set<Role> allRoles = EnumSet.allOf(Role.class);
        allRoles.removeAll(QuestCreateController.ALLOWED_ROLES_FOR_QUEST_CREATE);

        if (allRoles.isEmpty()) {
            return Stream.of(EMPTY_STREAM_MARKER);
        } else {
            return allRoles.stream().map(Enum::name);
        }
    }

    @Test
    void showCreateQuestPage_WhenUserNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.CREATE_QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(Route.REDIRECT + Route.LOGIN))
                .andExpect(flash().attribute(Key.SOURCE, Key.CREATE_QUEST));
    }

    @Test
    @Transactional
    public void createQuest_Success_ShouldRedirectToEditPageWithQuestId() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, validQuestName)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.QUEST_DESCRIPTION, validQuestDescription)
                        .param(Key.ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(Route.QUEST_EDIT + TestConstants.REDIRECT_QUERY_ID_TEMPLATE));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForMissingTest")
    void createQuest_WhenMissingParameters_ShouldReturnError(String missingParam) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(Key.QUEST_NAME, validQuestName);
        params.add(Key.QUEST_TEXT, validQuestText);
        params.add(Key.QUEST_DESCRIPTION, validQuestDescription);
        params.add(Key.ID, validUserId);

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
                        .param(Key.QUEST_NAME, paramName.equals(Key.QUEST_NAME) ? paramValue : validQuestName)
                        .param(Key.QUEST_TEXT, paramName.equals(Key.QUEST_TEXT) ? paramValue : validQuestText)
                        .param(Key.QUEST_DESCRIPTION, paramName.equals(Key.QUEST_DESCRIPTION) ? paramValue : validQuestDescription)
                        .param(Key.ID, paramName.equals(Key.ID) ? paramValue : validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    String actualRedirect = result.getResponse().getRedirectedUrl();
                    Assertions.assertTrue(Objects.requireNonNull(actualRedirect).startsWith(expectedRedirectStart));
                });
    }

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of(Key.QUEST_NAME, TestConstants.EMPTY_STRING, Route.QUEST_EDIT + TestConstants.REDIRECT_ANY_ID_URI_TEMPLATE),
                Arguments.of(Key.QUEST_TEXT, TestConstants.EMPTY_STRING, Key.CREATE_QUEST),
                Arguments.of(Key.QUEST_DESCRIPTION, TestConstants.EMPTY_STRING, Route.QUEST_EDIT + TestConstants.REDIRECT_ANY_ID_URI_TEMPLATE),
                Arguments.of(Key.ID, TestConstants.EMPTY_STRING, Key.CREATE_QUEST)
        );
    }

    @Test
    public void createQuest_WhenQuestTextIsInvalid_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        mockMvc.perform(post(Route.CREATE_QUEST)
                        .param(Key.QUEST_NAME, validQuestName)
                        .param(Key.QUEST_TEXT, invalidQuestText)
                        .param(Key.QUEST_DESCRIPTION, validQuestDescription)
                        .param(Key.ID, validUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Key.CREATE_QUEST))
                .andExpect(flash().attributeExists(Key.ERROR));
    }
}