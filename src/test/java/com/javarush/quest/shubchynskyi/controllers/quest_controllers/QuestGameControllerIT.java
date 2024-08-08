package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.entity.GameState;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.notNullValue;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestGameControllerIT {

    @Autowired
    private MockMvc mockMvc;

//    @Value("${valid.quest.name}") todo вытащить имя квеста из базы?
    private final String validQuestName = "Перед волшебным камнем";

    // Допустимые и недопустимые значения
    private static final String VALID_QUEST_ID = "1"; // ID существующего квеста
    private static final String INVALID_QUEST_ID = "999999"; // предполагаемый несуществующий ID
    private static final String VALID_QUESTION_ID = "1"; // ID существующего вопроса
    private static final String GAME_STATE_PLAY = GameState.PLAY.toString();
    private static final String GAME_STATE_WIN = GameState.WIN.toString();

    @Test
    void startQuest_WhenQuestExists_ShouldPrepareAndShowQuest() throws Exception {
        // Этот тест проверяет корректное начало квеста, если квест существует
        mockMvc.perform(get(Route.QUEST)
                        .param(ID, VALID_QUEST_ID)) // Выполняем GET запрос на URL /quest с параметром id
                .andExpect(status().isOk()) // Ожидаем, что статус ответа будет 200 OK
                .andExpect(view().name(Route.QUEST)); // Проверяем, что возвращается правильное имя view
    }

    @Test
    void startQuest_WhenQuestDoesNotExist_ShouldRedirectToQuestList() throws Exception {
        // Тест проверяет перенаправление на список квестов, если квест не найден
        mockMvc.perform(get(Route.QUEST)
                        .param(ID, INVALID_QUEST_ID)) // Выполняем GET запрос с недействительным ID квеста
                .andExpect(status().is3xxRedirection()) // Ожидаем, что будет выполнено перенаправление
                .andExpect(redirectedUrl(Route.QUESTS_LIST)); // Проверяем, что перенаправление идет на URL списка квестов
    }
    
    @ParameterizedTest
    @EnumSource(value = GameState.class, mode = EnumSource.Mode.EXCLUDE, names = {"PLAY"})
    void nextStep_WhenGameStateIsNotPlay_ShouldRedirectToQuestsList(GameState gameState) throws Exception {
        // Act & Assert
        mockMvc.perform(post(Route.QUEST)
                        .param(GAME_STATE, gameState.toString())
                        .param(QUESTION_ID, VALID_QUESTION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void nextStep_WhenQuestionIdProvidedAndGameStateIsPlay_ShouldRedirectToNextQuestion() throws Exception {
        // Тест проверяет перенаправление на следующий вопрос, если предоставлен ID вопроса и состояние игры PLAY
        mockMvc.perform(post(Route.QUEST)
                        .param(ID, VALID_QUEST_ID) // Передаем ID квеста
                        .param(GAME_STATE, GAME_STATE_PLAY) // Устанавливаем состояние игры Play
                        .param(QUESTION_ID, VALID_QUESTION_ID)) // Передаем ID вопроса
                .andExpect(status().is3xxRedirection()) // Ожидаем, что произойдет перенаправление
                .andExpect(redirectedUrlPattern("/quest?id=" + VALID_QUEST_ID + "&question=*")); // Проверяем, что перенаправление идет на URL с параметром следующего вопроса
    }


    @Test
    void nextStep_WhenQuestionIdNotProvidedAndGameStateIsPlay_ShouldRedirectToQuestsList() throws Exception {
        // Этот тест проверяет, что если ID вопроса не предоставлен, но состояние игры PLAY, метод nextStep перенаправляет на страницу со списком квестов
        mockMvc.perform(post(Route.QUEST)
                        .param(ID, VALID_QUEST_ID)
                        .param(GAME_STATE, GAME_STATE_PLAY)) // Устанавливаем состояние игры PLAY
                .andExpect(status().is3xxRedirection()) // Ожидаем, что произойдет перенаправление
                .andExpect(redirectedUrl(Route.QUESTS_LIST)); // Проверяем, что перенаправление идет на URL списка квестов
    }

    @Test
    void startQuest_WhenQuestExistsWithStartQuestion_ShouldPrepareStartAndShowQuest() throws Exception {

        mockMvc.perform(get(Route.QUEST)
                        .param(ID, VALID_QUEST_ID)
                        .param(QUESTION_ID, VALID_QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUEST))
                .andExpect(request().attribute(QUEST_NAME, validQuestName)) // // todo вытащить из квеста
                .andExpect(request().attribute(START_QUESTION_ID, 12L)); // todo вытащить из квеста
    }

    @Test
    void nextStep_WhenGameStateNotPlayAndQuestionIdProvided_ShouldRedirectToQuestList() throws Exception {
        mockMvc.perform(post(Route.QUEST)
                        .param(GAME_STATE, GameState.WIN.toString())
                        .param(QUESTION_ID, VALID_QUESTION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void nextStep_WhenGameStatePlayAndQuestionIdValid_ShouldRedirectToNextQuestion() throws Exception {
        mockMvc.perform(post(Route.QUEST)
                        .param(ID, VALID_QUEST_ID)
                        .param(GAME_STATE, GameState.PLAY.toString())
                        .param(QUESTION_ID, VALID_QUESTION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/quest?id=" + VALID_QUEST_ID + "&question=*"));
    }

    @Test
    void nextStep_WhenQuestionIdNotProvidedAndGameStatePlay_ShouldRedirectToQuestList() throws Exception {
        mockMvc.perform(post(Route.QUEST)
                        .param(GAME_STATE, GameState.PLAY.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void nextStep_WhenInvalidGameStateProvided_ShouldRedirectToQuestList() throws Exception {
        mockMvc.perform(post(Route.QUEST)
                        .param(GAME_STATE, "INVALID_GAME_STATE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void nextStep_WhenEverythingInvalid_ShouldStillRedirectToQuestList() throws Exception {
        mockMvc.perform(post(Route.QUEST))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.QUESTS_LIST));
    }

    @Test
    void startQuest_WhenQuestStarted_ShouldSetCorrectAttributes() throws Exception {

        mockMvc.perform(get(Route.QUEST)
                        .param(ID, VALID_QUEST_ID))
                .andExpect(status().isOk())
                .andExpect(request().attribute(QUEST_NAME, notNullValue())) // todo проверить фактические значения
                .andExpect(request().attribute(QUEST_DESCRIPTION, notNullValue()))
                .andExpect(request().attribute(QUEST_IMAGE, notNullValue()))
                .andExpect(request().attribute(START_QUESTION_ID, notNullValue()));
    }
}
