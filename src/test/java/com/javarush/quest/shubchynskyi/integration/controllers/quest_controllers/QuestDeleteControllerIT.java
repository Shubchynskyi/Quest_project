package com.javarush.quest.shubchynskyi.integration.controllers.quest_controllers;


import com.javarush.quest.shubchynskyi.config.RoleConfig;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class QuestDeleteControllerIT {

    @Value("${valid.quest.id}")
    private String validQuestId;
    @Value("${invalid.quest.incorrectId}")
    private String incorrectQuestId;
    @Value("${invalid.quest.id}")
    private String nonExistentId;
    @Value("${valid.user.id}")
    private String validUserId;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuestService questService;
    @Autowired
    private UserMapper userMapper;

    private UserDTO userDTO;
    private Quest testQuest;

    private Stream<Role> allowedRolesProvider() {
        return RoleConfig.ALLOWED_ROLES_FOR_QUEST_DELETE.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !RoleConfig.ALLOWED_ROLES_FOR_QUEST_DELETE.contains(role));
    }

    @BeforeAll
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(Long.valueOf(validUserId));
        userDTO.setRole(Role.USER);
        userDTO.setQuests(new java.util.ArrayList<>());

        Optional<Quest> optionalQuest = questService.get(validQuestId);
        if (optionalQuest.isPresent()) {
            testQuest = optionalQuest.get();
            testQuest.setAuthor(userMapper.userDTOToUser(userDTO));
        } else {
            throw new IllegalStateException(ERROR_TEST_QUEST_NOT_FOUND_WITH_ID + validQuestId);
        }
    }

    @Test
    void deleteQuest_WhenUserNotInSession_ShouldRedirectToQuestsList() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void deleteQuest_WithAllowedRoles_ShouldDeleteSuccessfully(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);
        userDTO.setId(testQuest.getAuthor().getId() + 1);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL));

        Optional<Quest> deletedQuest = questService.get(validQuestId);
        assertTrue(deletedQuest.isEmpty(), ASSERT_QUEST_DELETE);
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void deleteQuest_WithDisallowedRoles_ShouldRedirectWithError(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);
        userDTO.setId(testQuest.getAuthor().getId() + 1);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL))
                .andExpect(flash().attributeExists(ERROR));

        Optional<Quest> deletedQuest = questService.get(validQuestId);
        assertTrue(deletedQuest.isPresent(), ASSERT_QUEST_NOT_DELETED);
    }

    @Test
    void deleteQuest_WithUserRoleAndIsAuthor_ShouldDeleteSuccessfully() throws Exception {
        userDTO.setId(testQuest.getAuthor().getId());

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL));

        Optional<Quest> deletedQuest = questService.get(validQuestId);
        assertTrue(deletedQuest.isEmpty(), ASSERT_QUEST_DELETE);
    }

    @Test
    void deleteQuest_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, nonExistentId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL))
                .andExpect(flash().attributeExists(ERROR));
    }

    @Test
    void deleteQuest_WithUserRoleAndNotAuthor_ShouldRedirectWithError() throws Exception {
        userDTO.setId(testQuest.getAuthor().getId() + 1);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL))
                .andExpect(flash().attributeExists(ERROR));

        Optional<Quest> deletedQuest = questService.get(validQuestId);
        assertTrue(deletedQuest.isPresent(), ASSERT_QUEST_NOT_DELETED);
    }

    @Test
    void deleteQuest_ShouldUpdateUserQuestsInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, userDTO);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SOURCE_URL));

        assertFalse(userDTO.getQuests().stream()
                        .anyMatch(questDTO -> questDTO.getId().equals(Long.valueOf(validQuestId))),
                ASSERT_QUEST_REMOVED_FROM_USER_LIST);

        UserDTO updatedUser = (UserDTO) session.getAttribute(USER);
        assertNotNull(updatedUser, ASSERT_UPDATED_USER_NOT_NULL);
        assertFalse(updatedUser.getQuests().stream()
                        .anyMatch(questDTO -> questDTO.getId().equals(Long.valueOf(validQuestId))),
                ASSERT_QUEST_REMOVED_FROM_USER_SESSION_LIST);
    }

    @Test
    void deleteQuest_WithIncorrectIdFormat_ShouldHandleError() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, incorrectQuestId)
                        .param(SOURCE, SOURCE_URL)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attributeExists(ERROR));
    }

    @ParameterizedTest
    @ValueSource(strings = {"login", "profile", "source"})
    void deleteQuest_WithDifferentSources_ShouldRedirectCorrectly(String source) throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST_DELETE))
                        .param(ID, validQuestId)
                        .param(SOURCE, source)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(source));
    }

}