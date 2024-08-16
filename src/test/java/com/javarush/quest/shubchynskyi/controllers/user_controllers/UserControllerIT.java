package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import static com.javarush.quest.shubchynskyi.TestConstants.EMPTY_STRING;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIT {

    @Value("${valid.user.session.password}")
    public String sessionUserPassword;
    @Value("${valid.user.session.login}")
    public String sessionUserLogin;
    @Value("${valid.user.session.id}")
    public long sessionUserId;

    @Value("${valid.user.model.password}")
    public String modelUserPassword;
    @Value("${valid.user.model.login}")
    public String modelUserLogin;
    @Value("${valid.user.model.id}")
    public long modelUserId;
    @Value("${valid.user.model.role}")
    public String modelUserRole;

    @Value("${valid.user.id}")
    public String userIdForDelete;

    @Value("${app.images-directory}")
    private String imagesDirectory;
    @Value("${app.images.test-image.name}")
    private String testImage;
    @Value("${app.images.test-image.content-type}")
    private String contentType;

    @Autowired
    private MockMvc mockMvc;

    private UserDTO sessionUserDTO;
    private UserDTO modelUserDTO;

    @BeforeAll
    public void setup() {
        sessionUserDTO = createUserDTO(sessionUserId, sessionUserLogin, sessionUserPassword, null);
        modelUserDTO = createUserDTO(modelUserId, modelUserLogin, modelUserPassword, Role.valueOf(modelUserRole));
    }

    private UserDTO createUserDTO(long id, String login, String password, Role role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setLogin(login);
        userDTO.setPassword(password);
        if (role != null) {
            userDTO.setRole(role);
        }
        return userDTO;
    }

    private MockHttpSession createSessionWithRole(Role role) {
        MockHttpSession session = new MockHttpSession();
        sessionUserDTO.setRole(role);
        session.setAttribute(Key.USER, sessionUserDTO);
        return session;
    }

    private MockMultipartFile createMockImage() throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(imagesDirectory + "/" + testImage));
        return new MockMultipartFile(Key.IMAGE, testImage, contentType, fileContent);
    }

    // todo take from config
    private static List<Role> allowedRoles() {
        return Arrays.asList(Role.ADMIN, Role.MODERATOR, Role.USER);
    }
    // todo take from config
    private static List<Role> disallowedRoles() {
        return List.of(Role.GUEST);
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    public void whenGetUserById_ForAllowedRoles_ThenShowUserData(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);

        mockMvc.perform(get(Route.USER)
                        .session(session)
                        .param(Key.ID, sessionUserDTO.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USER))
                .andExpect(model().attribute(Key.USER, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("disallowedRoles")
    public void whenGetUserById_ForDisallowedRoles_ThenRedirectToIndex(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);

        mockMvc.perform(get(Route.USER)
                        .session(session)
                        .param(Key.ID, sessionUserDTO.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX));
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    @Transactional
    public void whenEditUser_ForAllowedRoles_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);
        MockMultipartFile mockImage = createMockImage();

        session.setAttribute(Key.ORIGINAL_LOGIN, sessionUserDTO.getLogin());

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(Key.ID, sessionUserDTO.getId().toString())
                        .param(Key.LOGIN, sessionUserDTO.getLogin())
                        .param(Key.PASSWORD, sessionUserDTO.getPassword())
                        .param(Key.ROLES, sessionUserDTO.getRole().name())
                        .param(Key.UPDATE, EMPTY_STRING)
                        .param(Key.TEMP_IMAGE_ID, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    @Transactional
    public void whenEditAnotherUser_ForAllowedRoles_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);
        MockMultipartFile mockImage = createMockImage();

        session.setAttribute(Key.ORIGINAL_LOGIN, modelUserDTO.getLogin());

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(Key.ID, modelUserDTO.getId().toString())
                        .param(Key.LOGIN, modelUserDTO.getLogin())
                        .param(Key.PASSWORD, modelUserDTO.getPassword())
                        .param(Key.ROLES, modelUserDTO.getRole().name())
                        .sessionAttr(Key.SOURCE, Route.USERS)
                        .param(Key.UPDATE, EMPTY_STRING)
                        .param(Key.TEMP_IMAGE_ID, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.USERS));
    }

    @ParameterizedTest
    @MethodSource("disallowedRoles")
    @Transactional
    public void whenEditUser_ForDisallowedRoles_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);
        MockMultipartFile mockImage = createMockImage();

        session.setAttribute(Key.ORIGINAL_LOGIN, sessionUserDTO.getLogin());

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(Key.ID, sessionUserDTO.getId().toString())
                        .param(Key.LOGIN, sessionUserDTO.getLogin())
                        .param(Key.PASSWORD, sessionUserDTO.getPassword())
                        .param(Key.ROLES, sessionUserDTO.getRole().name())
                        .param(Key.UPDATE, EMPTY_STRING)
                        .param(Key.TEMP_IMAGE_ID, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @ParameterizedTest
    @MethodSource("allowedRoles")
    @Transactional
    public void whenDeleteUser_ForAllowedRoles_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);
        session.setAttribute(Key.USER, sessionUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, userIdForDelete)
                        .param(Key.DELETE, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.USERS));

        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, sessionUserDTO.getId().toString())
                        .param(Key.DELETE, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.LOGOUT));
    }

    @ParameterizedTest
    @MethodSource("disallowedRoles")
    @Transactional
    public void whenDeleteUser_ForDisallowedRoles_ThenReturnCorrectStatus(Role accessRole) throws Exception {
        MockHttpSession session = createSessionWithRole(accessRole);
        session.setAttribute(Key.USER, sessionUserDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(Key.ID, userIdForDelete)
                        .param(Key.DELETE, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }
}