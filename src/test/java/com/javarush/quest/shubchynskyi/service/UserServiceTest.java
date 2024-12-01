package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.javarush.quest.shubchynskyi.TestConstants.*;
import static com.javarush.quest.shubchynskyi.constant.Key.USER_NOT_FOUND_WITH_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .login(TEST_USER_LOGIN)
                .password(TEST_USER_PASSWORD)
                .role(Role.USER)
                .build();
    }

    @Test
    void should_ReturnTrue_When_LoginExists() {
        when(userRepository.exists(any(Example.class))).thenReturn(true);

        boolean exists = userService.isLoginExist(TEST_USER_LOGIN);

        assertTrue(exists);
        verify(userRepository, times(1)).exists(any(Example.class));
    }

    @Test
    void should_ReturnFalse_When_LoginDoesNotExist() {
        when(userRepository.exists(any(Example.class))).thenReturn(false);

        boolean exists = userService.isLoginExist(TEST_USER_LOGIN);

        assertFalse(exists);
        verify(userRepository, times(1)).exists(any(Example.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void should_ReturnFalse_When_LoginIsInvalid(String login) {
        when(userRepository.exists(any(Example.class))).thenReturn(false);

        boolean exists = userService.isLoginExist(login);

        assertFalse(exists);
        verify(userRepository, times(1)).exists(any(Example.class));
    }

    @Test
    void should_ReturnUser_When_UserIsValidForCreation() {
        String hashedPassword = "hashed_" + TEST_USER_PASSWORD;
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(TEST_USER_ID);
            return user;
        });

        User userToCreate = User.builder()
                .login(TEST_USER_LOGIN)
                .password(TEST_USER_PASSWORD)
                .role(Role.USER)
                .build();

        Optional<User> result = userService.create(userToCreate);

        assertTrue(result.isPresent());
        User savedUser = result.get();
        assertEquals(TEST_USER_ID, savedUser.getId());
        assertEquals(TEST_USER_LOGIN, savedUser.getLogin());
        assertEquals(hashedPassword, savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        verify(passwordEncoder, times(1)).encode(TEST_USER_PASSWORD);
        verify(userRepository, times(1)).save(userToCreate);
    }

    @Test
    void should_UpdateExistingUser_WithoutChangingPassword() {
        User updatedUser = User.builder()
                .id(TEST_USER_ID)
                .login(UPDATED_USER_LOGIN)
                .password(null)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.update(updatedUser);

        assertTrue(result.isPresent());
        User savedUser = result.get();
        assertEquals(TEST_USER_ID, savedUser.getId());
        assertEquals(UPDATED_USER_LOGIN, savedUser.getLogin());
        assertEquals(TEST_USER_PASSWORD, savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());

        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(userRepository, times(1)).save(testUser);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void should_UpdateExistingUser_AndChangePassword_WhenProvided() {
        String newPassword = UPDATED_USER_PASSWORD;
        String hashedNewPassword = "hashed_" + newPassword;

        User updatedUser = User.builder()
                .id(TEST_USER_ID)
                .login(UPDATED_USER_LOGIN)
                .password(newPassword)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(hashedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.update(updatedUser);

        assertTrue(result.isPresent());
        User savedUser = result.get();
        assertEquals(TEST_USER_ID, savedUser.getId());
        assertEquals(UPDATED_USER_LOGIN, savedUser.getLogin());
        assertEquals(hashedNewPassword, savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());

        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void should_ThrowException_When_UserDoesNotExist_OnUpdate() {
        User updatedUser = User.builder()
                .id(TEST_USER_ID)
                .login(UPDATED_USER_LOGIN)
                .password(UPDATED_USER_PASSWORD)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.update(updatedUser);
        });

        assertEquals(USER_NOT_FOUND_WITH_ID + TEST_USER_ID, exception.getMessage());

        verify(userRepository, times(1)).findById(TEST_USER_ID);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_DeleteUser_When_UserExists() {
        doNothing().when(userRepository).delete(testUser);

        userService.delete(testUser);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void should_ReturnAllUsers_When_UsersExist() {
        User user1 = User.builder()
                .id(1L)
                .login("login1")
                .password("password1")
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .id(2L)
                .login("login2")
                .password("password2")
                .role(Role.ADMIN)
                .build();

        List<User> userList = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        Collection<Optional<User>> result = userService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(Optional.of(user1)));
        assertTrue(result.contains(Optional.of(user2)));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void should_ReturnEmptyList_When_NoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<Optional<User>> result = userService.getAll();

        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void should_ReturnUserById_When_UserExists() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.get(TEST_USER_ID);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @Test
    void should_ReturnEmptyOptionalById_When_UserDoesNotExist() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        Optional<User> result = userService.get(TEST_USER_ID);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @Test
    void should_ReturnUserByLoginAndPassword_When_UserExistsAndPasswordMatches() {
        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_USER_PASSWORD, testUser.getPassword())).thenReturn(true);

        Optional<User> result = userService.get(TEST_USER_LOGIN, TEST_USER_PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        verify(userRepository, times(1)).findByLogin(TEST_USER_LOGIN);
        verify(passwordEncoder, times(1)).matches(TEST_USER_PASSWORD, testUser.getPassword());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_UserDoesNotExist() {
        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.empty());

        Optional<User> result = userService.get(TEST_USER_LOGIN, TEST_USER_PASSWORD);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findByLogin(TEST_USER_LOGIN);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_PasswordIsIncorrect() {
        when(userRepository.findByLogin(TEST_USER_LOGIN)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(INVALID_USER_PASSWORD, testUser.getPassword())).thenReturn(false);

        Optional<User> result = userService.get(TEST_USER_LOGIN, INVALID_USER_PASSWORD);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findByLogin(TEST_USER_LOGIN);
        verify(passwordEncoder, times(1)).matches(INVALID_USER_PASSWORD, testUser.getPassword());
    }

    @ParameterizedTest
    @CsvSource({
            "'', TEST_PASSWORD",
            "TEST_LOGIN, ''"
    })
    void should_ReturnEmptyOptionalByLoginAndPassword_When_LoginOrPasswordIsEmpty(String login, String password) {
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        Optional<User> result = userService.get(login, password);

        assertEquals(Optional.empty(), result);

        verify(userRepository, times(1)).findByLogin(login);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
