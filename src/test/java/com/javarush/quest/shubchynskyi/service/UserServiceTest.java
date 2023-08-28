package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static final Long TEST_USER_ID = 1L;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .login("testLogin")
                .password("testPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void should_ReturnTrue_When_LoginExists() {
        when(userRepository.exists(any())).thenReturn(true);

        assertTrue(userService.isLoginExist("testLogin"));
        verify(userRepository, times(1)).exists(any());
    }

    @Test
    void should_ReturnFalse_When_LoginDoesNotExist() {
        when(userRepository.exists(any())).thenReturn(false);

        assertFalse(userService.isLoginExist("testLogin"));
        verify(userRepository, times(1)).exists(any());
    }

    @Test
    void should_ReturnUser_When_UserIsValidForCreation() {
        when(userRepository.save(any())).thenReturn(testUser);

        assertEquals(Optional.of(testUser), userService.create(testUser));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void should_UpdateUser_When_UserIsValidForUpdate() {
        when(userRepository.save(any())).thenReturn(testUser);

        assertEquals(Optional.of(testUser), userService.update(testUser));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void should_DeleteUser_When_UserExists() {
        doNothing().when(userRepository).delete(any());

        userService.delete(testUser);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void should_ReturnAllUsers_When_UsersExist() {
        User user1 = User.builder().login("login1").password("password1").build();
        User user2 = User.builder().login("login2").password("password2").build();
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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        assertEquals(Optional.of(testUser), userService.get(TEST_USER_ID));
        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @Test
    void should_ReturnEmptyOptionalById_When_UserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), userService.get(TEST_USER_ID));
        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @Test
    void should_ReturnUserByLoginAndPassword_When_UserExists() {
        when(userRepository.findAll(ArgumentMatchers.<Example<User>>any()))
                .thenReturn(List.of(testUser));

        assertEquals(Optional.of(testUser), userService.get("testLogin", "testPassword"));
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Example<User>>any());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_UserDoesNotExist() {
        when(userRepository.findAll(ArgumentMatchers.<Example<User>>any()))
                .thenReturn(List.of());

        assertEquals(Optional.empty(), userService.get("testLogin", "testPassword"));
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Example<User>>any());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_PasswordIsIncorrect() {
        when(userRepository.findAll(ArgumentMatchers.<Example<User>>any()))
                .thenReturn(List.of());

        assertEquals(Optional.empty(), userService.get("testLogin", "wrongPassword"));
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Example<User>>any());
    }

    @Test
    void should_ReturnFalse_When_NullLogin() {
        when(userRepository.exists(any())).thenReturn(false);

        assertFalse(userService.isLoginExist(null));
        verify(userRepository, times(1)).exists(any());
    }

    @Test
    void should_ReturnFalse_When_EmptyLogin() {
        when(userRepository.exists(any())).thenReturn(false);

        assertFalse(userService.isLoginExist(""));
        verify(userRepository, times(1)).exists(any());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_EmptyLogin() {
        assertEquals(Optional.empty(), userService.get("", "testPassword"));
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Example<User>>any());
    }

    @Test
    void should_ReturnEmptyOptionalByLoginAndPassword_When_EmptyPassword() {
        assertEquals(Optional.empty(), userService.get("testLogin", ""));
        verify(userRepository, times(1)).findAll(ArgumentMatchers.<Example<User>>any());
    }
}
