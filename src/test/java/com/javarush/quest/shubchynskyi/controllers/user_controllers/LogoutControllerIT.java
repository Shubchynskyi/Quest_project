package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogoutControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenUserLogsOut_ThenSessionIsInvalidated_And_RedirectToIndex() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get(Route.LOGOUT).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(result -> {
                    MockHttpSession returnedSession = (MockHttpSession) result.getRequest().getSession(false);
                    assertTrue(returnedSession == null || returnedSession.isInvalid(), TestConstants.SESSION_SHOULD_BE_INVALIDATED_AFTER_LOGOUT);
                });
    }
}