package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final String VALID_LOGIN = "admin";
    private final String INVALID_LOGIN = "admin1";
    private final String VALID_PASSWORD = "admin";
    private final String INVALID_PASSWORD = "admin1";

    @Test
    public void whenUserLogsInWithValidCredentials_ThenUserIsAuthenticated_And_RedirectToProfile() throws Exception {
        mockMvc.perform(post(Route.LOGIN)
                        .param(Key.LOGIN, VALID_LOGIN)
                        .param(Key.PASSWORD, VALID_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(Key.USER, notNullValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {INVALID_LOGIN, VALID_LOGIN})
    public void whenUserLogsInWithInvalidCredentials_ThenUserIsNotAuthenticated_And_RedirectToLogin(String login) throws Exception {
        mockMvc.perform(post(Route.LOGIN)
                        .param(Key.LOGIN, login)
                        .param(Key.PASSWORD, INVALID_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(Key.ERROR, notNullValue()));
    }

    @Test
    public void whenUserIsAlreadyAuthenticated_ThenRedirectToProfile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Key.USER, new UserDTO()); // assuming UserDTO is the session attribute for authenticated users

        mockMvc.perform(post(Route.LOGIN)
                        .param(Key.LOGIN, VALID_LOGIN)
                        .param(Key.PASSWORD, VALID_PASSWORD)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    public void whenUserWithDifferentRolesLogsIn_ThenRedirectAccordingly(Role role) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(role);

        mockMvc.perform(post(Route.LOGIN)
                        .param(Key.LOGIN, VALID_LOGIN)
                        .param(Key.PASSWORD, VALID_PASSWORD)
                        .sessionAttr(Key.USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(getRedirectUrlForRole(role)));
    }

    // если админ например будет по умолчанию перенаправлен на страницу администратора, то логику внутри надо поменять
    private String getRedirectUrlForRole(Role role) {
        // return the appropriate redirect URL based on the user's role
        // this is just a placeholder, replace with your actual logic
        return Route.PROFILE;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/logins.csv")
    public void whenUserLogsInWithDifferentCases_ThenUserIsAuthenticated(String login, String password) throws Exception {
        mockMvc.perform(post(Route.LOGIN)
                        .param(Key.LOGIN, login)
                        .param(Key.PASSWORD, password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(Key.USER, notNullValue()));
    }

    @Test
    public void whenUserLogsInWithSpecialCharacters_ThenUserIsAuthenticated() throws Exception {
        mockMvc.perform(post(Route.LOGIN)
                        .param("login", "User@123")
                        .param("password", "Password#123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(Key.USER, notNullValue()));
    }

    @Test
    public void whenUserLogsInWithMaxLength_ThenUserIsNotAuthenticated() throws Exception {
        String login = new String(new char[256]).replace("\0", "a");
        String password = new String(new char[256]).replace("\0", "a");

        mockMvc.perform(post(Route.LOGIN)
                        .param("login", login)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(Key.ERROR, notNullValue()));
    }
}