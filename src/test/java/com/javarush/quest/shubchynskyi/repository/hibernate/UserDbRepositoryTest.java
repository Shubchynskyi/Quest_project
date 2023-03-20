package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.user.Role;
import com.javarush.quest.shubchynskyi.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDbRepositoryTest {

    private UserDbRepository userDbRepository;

    @BeforeEach
    void setup() {
        userDbRepository = new UserDbRepository(new SessionCreator());
    }

    @Test
    void get() {
        User user = userDbRepository.get(1L);
        assertEquals("admin", user.getLogin());
    }

    @Test
    void create() {
        User user = User.builder().login("test").password("password").role(Role.USER).build();
        userDbRepository.create(user);
        assertTrue(user.getId() > 0);
    }
}