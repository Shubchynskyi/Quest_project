package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.Role;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private static final String USER_ATTR = "user";
    private static final String FIELD_ERRORS_ATTR = "fieldErrors";
    private static final String ERROR_ATTR = "error";
    private static final String ERROR_MESSAGE = "error message";

    private UserDTO testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.USER;
        testUser = new UserDTO();
        testUser.setRole(testRole);
    }

    @Test
    void should_ProcessFieldErrors() {
        mockFieldErrors();

        validationService.processFieldErrors(bindingResult, redirectAttributes);

        verifyRedirectAttributesAddedOnceWith();
    }

    @Test
    void should_ReturnTrue_When_UserAccessIsDenied() {
        mockSessionUser(testUser);
        List<Role> validRoles = Collections.singletonList(Role.ADMIN);

        assertAccessIsDeniedForRoles(validRoles);
    }

    @Test
    void should_ReturnFalse_When_UserAccessIsGranted() {
        mockSessionUser(testUser);
        List<Role> validRoles = Collections.singletonList(testRole);

        assertAccessIsGrantedForRoles(validRoles);
    }

    @Test
    void should_ReturnTrue_When_UserIsNotAuthenticated() {
        mockSessionUser(null);

        assertAccessIsDeniedForRoles(Collections.singletonList(testRole));
    }

    @Test
    void should_HandleNoFieldErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        validationService.processFieldErrors(bindingResult, redirectAttributes);

        verifyRedirectAttributesAddedOnceWith();
    }

    @Test
    void should_ReturnFalse_When_UserHasOneOfMultipleValidRoles() {
        mockSessionUser(testUser);
        List<Role> validRoles = Arrays.asList(testRole, Role.ADMIN);

        assertAccessIsGrantedForRoles(validRoles);
    }

    @Test
    void should_ReturnTrue_When_UserHasInvalidRole() {
        mockSessionUser(testUser);
        List<Role> validRoles = List.of(Role.ADMIN);

        assertAccessIsDeniedForRoles(validRoles);
    }

    @Test
    void should_HandleNullUserObject() {
        mockSessionUser(null);

        assertAccessIsDeniedForRoles(Collections.singletonList(Role.USER));
    }

    private void mockFieldErrors() {
        List<FieldError> fieldErrors = List.of(new FieldError("objectName", "field", "defaultMessage"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(messageSource.getMessage(any(FieldError.class), any(Locale.class))).thenReturn(ERROR_MESSAGE);
    }

    private void mockSessionUser(UserDTO user) {
        when(httpSession.getAttribute(USER_ATTR)).thenReturn(user);
    }

    private void verifyRedirectAttributesAddedOnceWith() {
        verify(redirectAttributes, times(1)).addFlashAttribute(eq(ValidationServiceTest.FIELD_ERRORS_ATTR), any());
    }

    private void assertAccessIsDeniedForRoles(List<Role> validRoles) {
        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, validRoles, redirectAttributes);
        assertTrue(accessDenied);
        verify(messageSource).getMessage(eq(YOU_DONT_HAVE_PERMISSIONS), any(), any());
        verify(redirectAttributes).addFlashAttribute(eq(ERROR_ATTR), any());
    }

    private void assertAccessIsGrantedForRoles(List<Role> validRoles) {
        boolean accessDenied = validationService.checkUserAccessDenied(httpSession, validRoles, redirectAttributes);
        assertFalse(accessDenied);
        verify(redirectAttributes, never()).addFlashAttribute(eq(ERROR_ATTR), any());
    }
}
