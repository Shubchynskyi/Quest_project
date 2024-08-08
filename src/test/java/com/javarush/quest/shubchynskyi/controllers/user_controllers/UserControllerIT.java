package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.mapper.UserMapper;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private UserDTO validUserDTO;

    @Value("${valid.user.role}")
    private String validUserRoleString;

    @BeforeAll
    public void setup() {
        Role testUserRole = Role.valueOf(validUserRoleString.toUpperCase());

        validUserDTO = new UserDTO();
        validUserDTO.setId(5L);
        validUserDTO.setRole(testUserRole);
        validUserDTO.setPassword("Password#123");
        validUserDTO.setLogin("User@123");

    }

    private static List<Role> allowedRoles() {
        return Arrays.asList(Role.ADMIN, Role.MODERATOR, Role.USER);
    }

    private static List<Role> disallowedRoles() {
        return Arrays.asList(Role.GUEST);
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    public void whenGetUserById_ThenShowUserData(Role accessRole) throws Exception {
        MockHttpSession session = new MockHttpSession();
        validUserDTO.setRole(accessRole);
        session.setAttribute(Key.USER, validUserDTO);

        mockMvc.perform(get(Route.USER).session(session).param(Key.ID, validUserDTO.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USER))
                .andExpect(model().attribute(Key.USER, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    @Transactional
    public void whenEditUser_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = new MockHttpSession();

        validUserDTO.setRole(accessRole);

        // Загрузка файла изображения из ресурсов
        byte[] fileContent = Files.readAllBytes(Paths.get("src/test/resources/images/test-image.png"));
        MockMultipartFile mockImage = new MockMultipartFile("image", "test-image.png", "image/png", fileContent);

        session.setAttribute(Key.USER, validUserDTO);
        session.setAttribute("originalLogin", "User@123");


        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(Key.ID, "5")
                        .param("login", "User@123")
                        .param("password", "Password#123")
                        .param("role", "USER")
                        .param("update", "update")
                .param("tempImageId", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @MethodSource("disallowedRoles")
    @Transactional
    public void whenEditUser_ThenReturnCorrectStatusForDisallowedRoles(Role accessRole) throws Exception {
        MockHttpSession session = new MockHttpSession();

        validUserDTO.setRole(accessRole);

        // Загрузка файла изображения из ресурсов
        byte[] fileContent = Files.readAllBytes(Paths.get("src/test/resources/images/test-image.png"));
        MockMultipartFile mockImage = new MockMultipartFile("image", "test-image.png", "image/png", fileContent);

        session.setAttribute(Key.USER, validUserDTO);
        session.setAttribute("originalLogin", "User@123");

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(Key.ID, "5")
                        .param("login", "User@123")
                        .param("password", "Password#123")
                        .param("role", "USER")
                        .param("update", "update")
                        .param("tempImageId", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    @Transactional
    public void whenDeleteUser_ThenReturnCorrectStatusForAllowedRoles(Role accessRole) throws Exception {
        MockHttpSession session = new MockHttpSession();
        validUserDTO.setRole(accessRole);
        session.setAttribute(Key.USER, validUserDTO);

        // Тест для удаления другого пользователя
        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, "999") // ID другого пользователя
                        .param("delete", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.USERS));

        // Тест для удаления себя
        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, validUserDTO.getId().toString()) // ID текущего пользователя
                        .param("delete", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGOUT));
    }

    @ParameterizedTest
    @MethodSource("disallowedRoles")
    @Transactional
    public void whenDeleteUser_ThenReturnCorrectStatusForDisallowedRoles(Role accessRole) throws Exception {
        MockHttpSession session = new MockHttpSession();
        validUserDTO.setRole(accessRole);
        session.setAttribute(Key.USER, validUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, "999") // ID другого пользователя
                        .param("delete", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }
}