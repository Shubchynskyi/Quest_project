package com.javarush.quest.shubchynskyi.integration.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestPathResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerIT {

    @Value("${valid.user.model.login}")
    private String validLogin;
    @Value("${invalid.user.login}")
    private String invalidLogin;
    @Value("${valid.user.model.password}")
    private String validPassword;
    @Value("${invalid.user.password}")
    private String invalidPassword;
    @Value("${valid.user.special.login}")
    public String specialValidLogin;
    @Value("${valid.user.special.password}")
    public String specialValidPassword;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenUserLogsInWithValidCredentials_ThenUserIsAuthenticated_AndRedirectsToProfile() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, validLogin)
                        .param(PASSWORD, validPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(USER, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("loginsProvider")
    void whenUserLogsInWithInvalidCredentials_ThenUserIsNotAuthenticated_AndRedirectsToLogin(String login) throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, login)
                        .param(PASSWORD, invalidPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    private Stream<String> loginsProvider() {
        return Stream.of(validLogin, invalidLogin, specialValidLogin);
    }

    @Test
    void whenUserAlreadyAuthenticated_ThenRedirectsToProfile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, new UserDTO());

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, validLogin)
                        .param(PASSWORD, validPassword)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void whenUserLogsInWithDifferentRoles_ThenRedirectsAccordingly(Role role) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(role);

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, validLogin)
                        .param(PASSWORD, validPassword)
                        .sessionAttr(USER, userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/logins.csv")
    void whenUserLogsInWithDifferentCases_ThenUserIsAuthenticated(String login, String password) throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, login)
                        .param(PASSWORD, password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(USER, notNullValue()));
    }

    @Test
    void whenUserLogsInWithSpecialCharacters_ThenUserIsAuthenticated() throws Exception {
        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, specialValidLogin)
                        .param(PASSWORD, specialValidPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(request().sessionAttribute(USER, notNullValue()));
    }

    @Test
    void whenUserLogsInWithMaxLength_ThenUserIsNotAuthenticated() throws Exception {
        String login = new String(new char[256]).replace("\0", "a");
        String password = new String(new char[256]).replace("\0", "a");

        mockMvc.perform(post(TestPathResolver.resolvePath(Route.LOGIN))
                        .param(LOGIN, login)
                        .param(PASSWORD, password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGIN))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

}