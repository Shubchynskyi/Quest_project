package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIT {

    @Mock
    UserDTO userDTO = new UserDTO();

    @Autowired
    private MockMvc mockMvc;

    //todo take from config
    private static List<Role> roleProvider() {
        return Arrays.asList(Role.ADMIN, Role.MODERATOR);
    }

    @ParameterizedTest
    @MethodSource("roleProvider")
    public void whenUserHasAccessRole_ThenShowUsers(Role accessRole) throws Exception {
        when(userDTO.getRole()).thenReturn(accessRole);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Key.USER, userDTO);

        mockMvc.perform(get(Route.USERS).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USERS))
                .andExpect(model().attribute(Key.USERS, notNullValue()));
    }

    @Test
    public void whenUserHasNoAccessRole_ThenRedirectToIndex() throws Exception {
        mockMvc.perform(get(Route.USERS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(flash().attribute(Key.ERROR, notNullValue()));
    }
}