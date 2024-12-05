package com.javarush.quest.shubchynskyi.integration.controllers.user_controllers;


import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.javarush.quest.shubchynskyi.constant.Key.ID_URI_PATTERN;
import static com.javarush.quest.shubchynskyi.constant.Key.USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileControllerIT {

    @Value("${valid.user.role}")
    private String validUserRole;
    @Value("${valid.user.id}")
    private Long validUserId;

    @Autowired
    private MockMvc mockMvc;

    private UserDTO validUserDTO;
    private UserDTO invalidUserDTOWithNoId;

    @BeforeAll
    public void setup() {
        validUserDTO = new UserDTO();
        validUserDTO.setId(validUserId);
        validUserDTO.setRole(Role.valueOf(validUserRole));

        invalidUserDTOWithNoId = new UserDTO();
    }

    @Test
    void whenGetRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(TestPathResolver.resolvePath(Route.PROFILE)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    void whenGetRequestWithInvalidUser_ThenRedirectToLogin() throws Exception {
        MockHttpSession session = createSessionWithUser(invalidUserDTOWithNoId);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.PROFILE))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    void whenGetRequestWithValidUser_ThenReturnExpectedModel() throws Exception {
        MockHttpSession session = createSessionWithUser(validUserDTO);

        mockMvc.perform(get(TestPathResolver.resolvePath(Route.PROFILE))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute(USER, validUserDTO));
    }

    @Test
    void whenPostRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.PROFILE)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    void whenPostRequestWithInvalidUser_ThenRedirectToLogin() throws Exception {
        MockHttpSession session = createSessionWithUser(invalidUserDTOWithNoId);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.PROFILE))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    void whenPostRequestWithValidUser_ThenRedirectToExpectedUrl() throws Exception {
        MockHttpSession session = createSessionWithUser(validUserDTO);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.PROFILE))
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ID_URI_PATTERN.formatted(Route.USER, validUserDTO.getId())));
    }

    private MockHttpSession createSessionWithUser(UserDTO userDTO) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, userDTO);
        return session;
    }

}