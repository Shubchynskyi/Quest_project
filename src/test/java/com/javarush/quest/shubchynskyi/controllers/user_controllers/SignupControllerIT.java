package com.javarush.quest.shubchynskyi.controllers.user_controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.javarush.quest.shubchynskyi.constant.Key.ERROR;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignupControllerIT {

    @Autowired
    private MockMvc mockMvc;
    private UserDTO validUserDTO;
    private UserDTO newUserDTO;
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

        newUserDTO = new UserDTO();
        newUserDTO.setLogin("newUser");
        newUserDTO.setPassword("newPass12");
        newUserDTO.setRole(testUserRole);

        invalidUserDTOWithNoId = new UserDTO();
    }


    @Test
    public void whenUserIsNotAuthenticated_ThenShowSignup() throws Exception {
        mockMvc.perform(get(Route.SIGNUP))
                .andExpect(status().isOk())
                .andExpect(view().name(Route.SIGNUP));
    }

    @Test
    public void whenUserIsAuthenticated_ThenRedirectToProfileWithError() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Key.USER, validUserDTO);

        mockMvc.perform(get(Route.SIGNUP).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(Route.PROFILE))
                .andExpect(flash().attribute(ERROR, notNullValue()));
    }

    @Test
    public void whenShowSignup_ThenCorrectInitialState() throws Exception {
        mockMvc.perform(get(Route.SIGNUP))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userDTOFromModel", "roles", "tempImageId"))
                .andExpect(view().name(Route.SIGNUP));
    }

    @Test
    public void whenShowSignup_ThenAllAttributesPresent() throws Exception {
        mockMvc.perform(get(Route.SIGNUP))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userDTOFromModel"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("tempImageId"))
                .andExpect(view().name(Route.SIGNUP));
    }

    //**********************************
    @Test
    @Transactional
    public void whenSignupWithValidData_ThenRegisterAndRedirectToProfile() throws Exception {
        // Загрузка файла изображения из ресурсов
        byte[] fileContent = Files.readAllBytes(Paths.get("src/test/resources/images/test-image.png"));
        MockMultipartFile mockImage = new MockMultipartFile("image", "test-image.png", "image/png", fileContent);

        // Подготовка данных пользователя с уникальным логином
        String uniqueLogin = "newUser" + System.currentTimeMillis();

        // Использование mockImage и параметров newUserDTO в тесте
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                        .file(mockImage)
                        .param("login", uniqueLogin)
                        .param("password", "newPass12")
                        .param("role", Role.USER.name()) // Установка роли
                        .param("tempImageId", "") // tempImageId должен быть подготовлен заранее
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    @Test
    public void whenSignupWithInvalidData_ThenRedirectBackToSignupWithErrors() throws Exception {
        // Assuming the setup for a mock image file is similar to the working test
        byte[] fileContent = Files.readAllBytes(Paths.get("src/test/resources/images/test-image.png"));
        MockMultipartFile mockImage = new MockMultipartFile("image", "test-image.png", "image/png", fileContent);

        // Adjust the parameters to simulate invalid data, but include the mock file to avoid NullPointerException
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                        .file(mockImage) // Include the mock file
                        .param("login", "invalidUser") // Example of invalid data
                        .param("password", "") // Example of invalid data, assuming password cannot be empty
                        .param("tempImageId", "")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection()) // Adjust the expected outcome based on your application logic
                .andExpect(flash().attribute("fieldErrors", notNullValue()))
                .andExpect(redirectedUrl("/signup")); // Example expected outcome
    }


    // Test Registration with Already Authenticated User
    @Test
    public void whenAuthenticatedUserTriesToSignup_ThenRedirectToProfileWithError() throws Exception {
        // Создание сессии и добавление в нее атрибута пользователя для имитации аутентификации
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Key.USER, validUserDTO); // Предполагается, что validUserDTO - это действительный объект UserDTO

        // Выполнение запроса GET на эндпоинт регистрации с аутентифицированной сессией
        mockMvc.perform(get(Route.SIGNUP).session(session))
                .andExpect(status().is3xxRedirection()) // Ожидается, что будет выполнена переадресация
                .andExpect(redirectedUrl(Route.PROFILE)) // Ожидается, что переадресация будет на страницу профиля
                .andExpect(flash().attribute(ERROR, notNullValue())); // Проверка на наличие атрибута ошибки во flash-атрибутах
    }

}