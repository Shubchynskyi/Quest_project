package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.javarush.quest.shubchynskyi.TestConstants.*;
import static com.javarush.quest.shubchynskyi.constant.Key.*;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpSession httpSession;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ValidationService validationService;

    private UserDTO testUser;

    private static final List<Role> ALLOWED_ROLES = List.of(Role.ADMIN, Role.MODERATOR);
    private static final Role ALLOWED_ROLE = Role.ADMIN;
    private static final Role NOT_ALLOWED_ROLE = Role.USER;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setId(TEST_USER_ID);
    }

    @Test
    void should_ProcessFieldErrors() {
        FieldError fieldError = new FieldError(OBJECT_NAME, FIELD, DEFAULT_ERROR_MESSAGE);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage(any(FieldError.class), any(Locale.class))).thenReturn(ERROR_MESSAGE);

        boolean hasErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);

        assertTrue(hasErrors);
        verify(redirectAttributes).addFlashAttribute(eq(FIELD_ERRORS), any(Map.class));
    }

    @Test
    void should_HandleNoFieldErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);

        boolean hasErrors = validationService.processFieldErrors(bindingResult, redirectAttributes);

        assertFalse(hasErrors);
        verify(redirectAttributes, never()).addFlashAttribute(eq(FIELD_ERRORS), any());
    }

    @Test
    void should_GrantAccess_When_UserHasValidRole_And_QuestAuthorIdIsNull() {
        testUser.setRole(ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, ALLOWED_ROLES, redirectAttributes, null);

        assertFalse(accessDenied);
        verify(redirectAttributes, never()).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_GrantAccess_When_UserHasValidRole_And_QuestAuthorIdIsPresent() {
        testUser.setRole(ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, ALLOWED_ROLES, redirectAttributes, TEST_USER_ID + 1);

        assertFalse(accessDenied);
        verify(redirectAttributes, never()).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_GrantAccess_When_UserIsOwner_And_HasInvalidRole() {
        testUser.setRole(NOT_ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, ALLOWED_ROLES, redirectAttributes, TEST_USER_ID);

        assertFalse(accessDenied);
        verify(redirectAttributes, never()).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_DenyAccess_When_UserHasInvalidRole_And_IsNotOwner() {
        testUser.setRole(NOT_ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, ALLOWED_ROLES, redirectAttributes, TEST_USER_ID + 1);

        assertTrue(accessDenied);
        verify(messageSource).getMessage(eq(YOU_DONT_HAVE_PERMISSIONS), any(), any());
        verify(redirectAttributes).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_DenyAccess_When_UserIsNotAuthenticated() {
        mockSessionUser(null);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, ALLOWED_ROLES, redirectAttributes, TEST_USER_ID);

        assertTrue(accessDenied);
        verify(messageSource).getMessage(eq(YOU_DONT_HAVE_PERMISSIONS), any(), any());
        verify(redirectAttributes).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_DenyAccess_When_ValidRolesIsEmpty_And_UserIsNotOwner() {
        testUser.setRole(ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, Collections.emptyList(), redirectAttributes, TEST_USER_ID + 1);

        assertTrue(accessDenied);
        verify(messageSource).getMessage(eq(YOU_DONT_HAVE_PERMISSIONS), any(), any());
        verify(redirectAttributes).addFlashAttribute(eq(ERROR), any());
    }

    @Test
    void should_GrantAccess_When_UserIsOwner_And_ValidRolesIsEmpty() {
        testUser.setRole(NOT_ALLOWED_ROLE);
        mockSessionUser(testUser);

        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, Collections.emptyList(), redirectAttributes, TEST_USER_ID);

        assertFalse(accessDenied);
        verify(redirectAttributes, never()).addFlashAttribute(eq(ERROR), any());
    }

    private void mockSessionUser(UserDTO user) {
        when(httpSession.getAttribute(USER)).thenReturn(user);
    }
}