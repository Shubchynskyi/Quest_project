package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.javarush.quest.shubchynskyi.constant.Key.CREATE_QUEST;
import static com.javarush.quest.shubchynskyi.constant.Key.SOURCE;
import static com.javarush.quest.shubchynskyi.constant.Route.REDIRECT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
class QuestCreateControllerIT {

    @Autowired
    MockMvc mockMvc;

    private static String validQuestText;

//    @Value("${valid.quest.text.path}") // Инъекция пути из application.properties или application.yml
//    private String validQuestTextPath;

    private static final String VALID_QUEST_NAME = "Название квеста";
    private static final String VALID_QUEST_DESCRIPTION = "Описание квеста";
    private static final String VALID_USER_ID = "1"; //TODO вынести в настройки?
    private static final String INVALID_QUEST_TEXT = "Invalid text";

    @BeforeAll
    static void setUp() throws Exception {
        Path path = ResourceUtils.getFile("classpath:validQuestText.txt").toPath();
        validQuestText = Files.readString(path);
    }

    @Test
    public void showCreateQuestPage_WhenUserLoggedIn_ShouldReturnCreateQuestView() throws Exception {
        User user = new User();
        user.setRole(Role.USER);

        mockMvc.perform(get(Route.QUEST_CREATE).sessionAttr(Key.USER, user))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_QUEST));
    }

    @Test
    void showCreateQuestPage_WhenUserNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.QUEST_CREATE))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + Route.LOGIN))
                .andExpect(flash().attributeExists(SOURCE));
    }

    @Test
    @Transactional
    public void createQuest_Success_ShouldRedirectToEditPageWithQuestId() throws Exception {
        mockMvc.perform(post(Route.QUEST_CREATE)
                        .param(Key.QUEST_NAME, VALID_QUEST_NAME)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(Route.QUEST_EDIT + "?id={id}"));
    }

    // todo как проверить отсутствие каждого параметра в одном тесте?
    @Test
    public void createQuest_WhenMissingParameters_ShouldReturnError() throws Exception {
        mockMvc.perform(post(Route.QUEST_CREATE)
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().isBadRequest());
    }

    // todo сделать параметризованным и проверять каждый из параметров на ошибку
    @Test
    public void createQuest_WhenInvalidData_ShouldReturnError() throws Exception {
        mockMvc.perform(post(Route.QUEST_CREATE)
                        .param(Key.QUEST_NAME, Key.REGEX_EMPTY_STRING) // Неверные данные
                        .param(Key.QUEST_TEXT, validQuestText)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(Route.QUEST_EDIT + "?id={id}"));
    }

    // todo тут проверяем только ошибку в тексте, надо бы переназвать
    @Test
    public void createQuest_WhenServiceThrowsException_ShouldRedirectToCreateQuestPageWithError() throws Exception {
        mockMvc.perform(post(Route.QUEST_CREATE)
                        .param(Key.QUEST_NAME, VALID_QUEST_NAME)
                        .param(Key.QUEST_TEXT, INVALID_QUEST_TEXT)
                        .param(Key.QUEST_DESCRIPTION, VALID_QUEST_DESCRIPTION)
                        .param(Key.ID, VALID_USER_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CREATE_QUEST))
                .andExpect(flash().attributeExists(Key.ERROR));
    }
}
