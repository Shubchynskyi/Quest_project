package com.javarush.quest.shubchynskyi.controllers.user_controllers;


import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.javarush.quest.shubchynskyi.constant.Key.ID_URI_PATTERN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;
    private UserDTO validUserDTO;
    private UserDTO invalidUserDTOWithNoId;

    @Value("${valid.user.role}")
    private String validUserRoleString;
    @Value("${valid.user.id}")
    private Long validUserId;

    @BeforeAll
    public void setup() {
        Role testUserRole = Role.valueOf(validUserRoleString.toUpperCase());

        validUserDTO = new UserDTO();
        validUserDTO.setId(validUserId);
        validUserDTO.setRole(testUserRole);

        invalidUserDTOWithNoId = new UserDTO();
    }

    private MockHttpSession createSessionWithUser(UserDTO userDTO) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Key.USER, userDTO);
        return session;
    }

    @Test
    public void whenGetRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(get(Route.PROFILE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    public void whenGetRequestWithInvalidUser_ThenRedirectToLogin() throws Exception {
        MockHttpSession session = createSessionWithUser(invalidUserDTOWithNoId);

        mockMvc.perform(get(Route.PROFILE).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    public void whenGetRequestWithValidUser_ThenReturnExpectedModel() throws Exception {
        MockHttpSession session = createSessionWithUser(validUserDTO);

        mockMvc.perform(get(Route.PROFILE).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute(Key.USER, validUserDTO));
    }

    @Test
    public void whenPostRequestWithoutUser_ThenRedirectToLogin() throws Exception {
        mockMvc.perform(post(Route.PROFILE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    public void whenPostRequestWithInvalidUser_ThenRedirectToLogin() throws Exception {
        MockHttpSession session = createSessionWithUser(invalidUserDTOWithNoId);

        mockMvc.perform(post(Route.PROFILE).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN));
    }

    @Test
    public void whenPostRequestWithValidUser_ThenRedirectToExpectedUrl() throws Exception {
        MockHttpSession session = createSessionWithUser(validUserDTO);

        mockMvc.perform(post(Route.PROFILE).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ID_URI_PATTERN.formatted(Route.USER, validUserDTO.getId())));
    }
}

