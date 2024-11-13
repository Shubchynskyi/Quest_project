package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Route;
import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.directories.images}")
    private String imagesDirectory;
    @Value("${app.images.test-image.name}")
    private String testImage;
    @Value("${app.images.test-image.content-type}")
    private String contentType;

    @Value("${valid.user.new_user.login}")
    private String validUserLogin;
    @Value("${valid.user.new_user.password}")
    private String validUserPassword;
    @Value("${invalid.user.login}")
    private String invalidUserLogin;
    @Value("${valid.user.role}")
    private String validUserRoleString;
    @Value("${valid.user.id}")
    private Long validUserId;

    private UserDTO validUserDTO;
    private String testImagePath;

    @BeforeAll
    public void setup() {
        Role testUserRole = Role.valueOf(validUserRoleString.toUpperCase());
        validUserDTO = new UserDTO();
        validUserDTO.setId(validUserId);
        validUserDTO.setLogin(validUserLogin);
        validUserDTO.setPassword(validUserPassword);
        validUserDTO.setRole(testUserRole);
        testImagePath = Paths.get(imagesDirectory, testImage).toString();
    }

    private MockHttpSession createSessionWithUser(UserDTO userDTO) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER, userDTO);
        return session;
    }

    private MockMultipartFile createMockImage() throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(testImagePath));
        return new MockMultipartFile(IMAGE, testImage, contentType, fileContent);
    }

    @Test
    void whenGetSignupPageWithoutUser_ThenShowSignup() throws Exception {
        mockMvc.perform(get(Route.SIGNUP))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(USER_DTO_FROM_MODEL, ROLES, TEMP_IMAGE_ID))
                .andExpect(view().name(Route.SIGNUP));
    }

    @Test
    void whenAuthenticatedUserTriesToSignup_ThenRedirectToProfileWithError() throws Exception {
        MockHttpSession session = createSessionWithUser(validUserDTO);

        mockMvc.perform(get(Route.SIGNUP).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @Test
    @Transactional
    void whenSignupWithValidData_ThenRegisterAndRedirectToProfile() throws Exception {
        MockMultipartFile mockImage = createMockImage();
        String uniqueLogin = validUserLogin + System.currentTimeMillis();

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.SIGNUP)
                        .file(mockImage)
                        .param(LOGIN, uniqueLogin)
                        .param(PASSWORD, validUserPassword)
                        .param(ROLE, Role.USER.name())
                        .param(TEMP_IMAGE_ID, EMPTY_STRING)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE));
    }

    @Test
    void whenSignupWithInvalidData_ThenRedirectBackToSignupWithErrors() throws Exception {
        MockMultipartFile mockImage = createMockImage();

        mockMvc.perform(MockMvcRequestBuilders.multipart(Route.SIGNUP)
                        .file(mockImage)
                        .param(LOGIN, invalidUserLogin)
                        .param(PASSWORD, EMPTY_STRING)
                        .param(TEMP_IMAGE_ID, EMPTY_STRING)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(FIELD_ERRORS, notNullValue()))
                .andExpect(redirectedUrl(Route.SIGNUP));
    }
}
