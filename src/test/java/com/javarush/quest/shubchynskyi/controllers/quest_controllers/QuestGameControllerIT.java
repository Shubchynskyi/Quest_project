package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.TestConstants.REDIRECT_URL_PATTERN_NEXT_QUESTION;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestGameControllerIT {

    @Value("${game.states.invalid}")
    private String gameStateInvalid;
    @Value("${game.states.play}")
    private String gameStatePlay;
    @Value("${game.states.win}")
    private String gameStateWin;

    @Value("${valid.quest.id}")
    private String validQuestId;
    @Value("${invalid.quest.id}")
    private String invalidQuestId;
    @Value("${valid.quest.question-id}")
    private String validQuestionId;
    @Value("${valid.quest.expected-name}")
    private String expectedQuestName;
    @Value("${valid.quest.start-question-id}")
    private long expectedStartQuestionId;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenQuestExists_ThenPrepareAndShowQuest() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST))
                        .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUEST));
    }

    @Test
    void whenQuestDoesNotExist_ThenRedirectToQuestList() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST))
                        .param(ID, invalidQuestId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @ParameterizedTest
    @MethodSource("gameStateWithoutPlayProvider")
    void whenGameStateIsNotPlay_ThenRedirectToQuestList(GameState gameState) throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST))
                        .param(GAME_STATE, gameState.toString())
                        .param(QUESTION_ID, validQuestionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    private Stream<GameState> gameStateWithoutPlayProvider() {
        return Arrays.stream(GameState.values())
                .filter(state -> !state.name().equals(gameStatePlay)); // Исключаем состояние
    }

    @Test
    void whenGameStateIsPlayAndQuestionIdProvided_ThenRedirectToNextQuestion() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST))
                        .param(ID, validQuestId)
                        .param(GAME_STATE, gameStatePlay)
                        .param(QUESTION_ID, validQuestionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(String.format(REDIRECT_URL_PATTERN_NEXT_QUESTION, validQuestId)));
    }

    @Test
    void whenGameStateIsPlayAndQuestionIdNotProvided_ThenRedirectToQuestList() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST))
                        .param(ID, validQuestId)
                        .param(GAME_STATE, gameStatePlay))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void whenQuestExistsWithStartQuestion_ThenPrepareStartAndShowQuest() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST))
                        .param(ID, validQuestId)
                        .param(QUESTION_ID, validQuestionId))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUEST))
                .andExpect(request().attribute(QUEST_NAME, expectedQuestName))
                .andExpect(request().attribute(START_QUESTION_ID, expectedStartQuestionId));
    }

    @Test
    void whenGameStateIsNotPlayAndQuestionIdProvided_ThenRedirectToQuestList() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST))
                        .param(GAME_STATE, gameStateWin)
                        .param(QUESTION_ID, validQuestionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void whenInvalidGameStateProvided_ThenRedirectToQuestList() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST))
                        .param(GAME_STATE, gameStateInvalid))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void whenEverythingIsInvalid_ThenStillRedirectToQuestList() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.QUEST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void whenQuestStarted_ThenSetCorrectAttributes() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUEST))
                .param(ID, validQuestId))
                .andExpect(status().isOk())
                .andExpect(request().attribute(QUEST_NAME, notNullValue()))
                .andExpect(request().attribute(QUEST_DESCRIPTION, notNullValue()))
                .andExpect(request().attribute(QUEST_IMAGE, notNullValue()))
                .andExpect(request().attribute(START_QUESTION_ID, notNullValue()));
    }

}