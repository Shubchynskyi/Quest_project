package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogoutControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenUserLogsOut_ThenSessionIsInvalidated_And_RedirectToIndex() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get(Route.LOGOUT).session(session))
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(result -> {
                    MockHttpSession returnedSession = (MockHttpSession)result.getRequest().getSession(false);
                    assert returnedSession == null || returnedSession.isInvalid();
                });
    }
}
