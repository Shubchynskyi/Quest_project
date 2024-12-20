package com.javarush.quest.shubchynskyi.integration.controllers.quest_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
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

    @Value("${app.tests.expectedQuestsSize}")
    private int expectedQuestsSize;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenShowQuests_ThenShouldReturnQuestsList() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.QUESTS_LIST)))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.QUESTS_LIST))
                .andExpect(model().attributeExists(QUESTS))
                .andExpect(model().attribute(QUESTS, hasSize(expectedQuestsSize)));
    }

}