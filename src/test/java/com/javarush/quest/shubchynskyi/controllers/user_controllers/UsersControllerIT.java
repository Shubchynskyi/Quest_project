package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private UserDTO userDTO;

    @BeforeAll
    public void setup() {
        userDTO = new UserDTO();
    }

    //todo take from config
    private static List<Role> roleProvider() {
        return Arrays.asList(Role.ADMIN, Role.MODERATOR);
    }

    @ParameterizedTest
    @MethodSource("roleProvider")
    public void whenUserHasAccessRole_ThenShowUsers(Role accessRole) throws Exception {
        userDTO.setRole(accessRole);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, userDTO);

        mockMvc.perform(get(Route.USERS)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USERS))
                .andExpect(model().attribute(USERS, notNullValue()));
    }

    @Test
    public void whenUserHasAccessRole_ThenShowUsers() throws Exception {
        mockMvc.perform(get(Route.USERS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

}