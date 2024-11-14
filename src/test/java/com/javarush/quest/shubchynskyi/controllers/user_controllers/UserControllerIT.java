package com.javarush.quest.shubchynskyi.controllers.user_controllers;

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
import java.util.EnumSet;
import java.util.stream.Stream;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
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
    @Value("${valid.user.session.role}")
    public String sessionUserBaseRole;

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

    @Value("${app.directories.images}")
    private String imagesDirectory;
    @Value("${app.images.test-image.name}")
    private String testImage;
    @Value("${app.images.test-image.content-type}")
    private String contentType;

    @Autowired
    private MockMvc mockMvc;

    private UserDTO sessionUserDTO;
    private UserDTO modelUserDTO;
    private String testImagePath;

    @BeforeAll
    public void setup() {
        sessionUserDTO = createUserDTO(sessionUserId, sessionUserLogin, sessionUserPassword, Role.valueOf(sessionUserBaseRole));
        modelUserDTO = createUserDTO(modelUserId, modelUserLogin, modelUserPassword, Role.valueOf(modelUserRole));
        testImagePath = Paths.get(imagesDirectory, testImage).toString();
    }

    private UserDTO createUserDTO(long id, String login, String password, Role role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setLogin(login);
        userDTO.setPassword(password);
        userDTO.setRole(role);
        return userDTO;
    }

    private MockHttpSession createSessionWithRole(Role role) {
        MockHttpSession session = new MockHttpSession();
        sessionUserDTO.setRole(role);
        session.setAttribute(USER, sessionUserDTO);
        return session;
    }

    private MockMultipartFile createMockImage() throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(testImagePath));
        return new MockMultipartFile(IMAGE, testImage, contentType, fileContent);
    }

    private Stream<Role> allowedRolesProvider() {
        return UserController.ALLOWED_ROLES_FOR_USER_EDIT.stream();
    }

    private Stream<Role> notAllowedRolesProvider() {
        return EnumSet.allOf(Role.class).stream()
                .filter(role -> !UserController.ALLOWED_ROLES_FOR_USER_EDIT.contains(role));
    }

    private void performDeleteUserAction(MockHttpSession session, String userId, String expectedRedirectUrl) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(Route.USER)
                        .session(session)
                        .param(ID, userId)
                        .param(DELETE, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    private void performUserEditAction(MockHttpSession session, UserDTO userDTO, String source, String expectedRedirectUrl) throws Exception {
        MockMultipartFile mockImage = createMockImage();
        session.setAttribute(ORIGINAL_LOGIN, userDTO.getLogin());

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.USER)
                        .file(mockImage)
                        .session(session)
                        .param(ID, userDTO.getId().toString())
                        .param(LOGIN, userDTO.getLogin())
                        .param(PASSWORD, userDTO.getPassword())
                        .param(ROLES, userDTO.getRole().name())
                        .sessionAttr(SOURCE, source)
                        .param(UPDATE, EMPTY_STRING)
                        .param(TEMP_IMAGE_ID, EMPTY_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    void whenUserGetsUserByIdWithAllowedRoles_ThenShowUserData(Role allowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(allowedRole);

        mockMvc.perform(get(Route.USER)
                        .session(session)
                        .param(ID, sessionUserDTO.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.USER))
                .andExpect(model().attribute(USER, notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    void whenUserGetsUserByIdWithDisallowedRoles_ThenRedirectToIndex(Role notAllowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(notAllowedRole);

        mockMvc.perform(get(Route.USER)
                        .session(session)
                        .param(ID, sessionUserDTO.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.INDEX));
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    @Transactional
    void whenUserEditsCurrentUserWithAllowedRoles_ThenRedirectToProfile(Role allowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(allowedRole);

        performUserEditAction(session, sessionUserDTO, Route.PROFILE, Route.PROFILE);
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    @Transactional
    void whenUserEditsAnotherUserWithAllowedRoles_ThenRedirectToUsers(Role allowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(allowedRole);

        performUserEditAction(session, modelUserDTO, Route.USERS, Route.USERS);
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    @Transactional
    void whenUserEditsCurrentUserWithDisallowedRoles_ThenRedirectToProfile(Role notAllowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(notAllowedRole);

        performUserEditAction(session, sessionUserDTO, Route.PROFILE, Route.PROFILE);
    }

    @ParameterizedTest
    @MethodSource("allowedRolesProvider")
    @Transactional
    void whenUserDeletesUserWithAllowedRoles_ThenRedirectCorrectly(Role allowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(allowedRole);

        performDeleteUserAction(session, userIdForDelete, Route.USERS);
        performDeleteUserAction(session, sessionUserDTO.getId().toString(), Route.LOGOUT);
    }

    @ParameterizedTest
    @MethodSource("notAllowedRolesProvider")
    @Transactional
    void whenUserDeletesUserWithDisallowedRoles_ThenRedirectToProfile(Role notAllowedRole) throws Exception {
        MockHttpSession session = createSessionWithRole(notAllowedRole);

        performDeleteUserAction(session, userIdForDelete, Route.PROFILE);
    }

}