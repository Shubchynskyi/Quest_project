package com.javarush.quest.shubchynskyi.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;

import static com.javarush.quest.shubchynskyi.constant.Key.QUESTS;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestsListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.tests.expectedQuestsSize}")
    private int expectedQuestsSize;

    @Test
    void whenShowQuests_ThenShouldReturnQuestsList() throws Exception {
        mockMvc.perform(get(Route.QUESTS_LIST))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUESTS_LIST))
                .andExpect(model().attributeExists(QUESTS))
                .andExpect(model().attribute(QUESTS, hasSize(expectedQuestsSize)));
    }

}