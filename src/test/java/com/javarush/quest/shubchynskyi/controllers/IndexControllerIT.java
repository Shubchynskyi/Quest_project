package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetIndexCalled_ThenShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get(Route.INDEX))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.INDEX_PAGE));
    }
}