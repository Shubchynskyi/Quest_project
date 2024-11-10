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
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.stream.Stream;

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

    private Stream<Role> allowedRolesProvider() {
        return UsersController.ALLOWED_ROLES_FOR_USERS_LIST.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !UsersController.ALLOWED_ROLES_FOR_USERS_LIST.contains(role));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    public void whenUserHasAccessRole_ThenShowUsers(Role allowedRole) throws Exception {
        userDTO.setRole(allowedRole);

        mockMvc.perform(get(Route.USERS)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USERS))
                .andExpect(model().attribute(USERS, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    public void whenUserHasNoAccessRole_ThenRedirectToIndexWithError(Role notAllowedRole) throws Exception {
        userDTO.setRole(notAllowedRole);

        mockMvc.perform(get(Route.USERS)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @Test
    public void whenUserIsNotAuthenticated_ThenRedirectToIndexWithError() throws Exception {
        mockMvc.perform(get(Route.USERS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }
}