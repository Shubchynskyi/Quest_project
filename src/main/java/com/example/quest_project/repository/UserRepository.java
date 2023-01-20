package com.example.quest_project.repository;

import com.example.quest_project.entity.Role;
import com.example.quest_project.entity.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * repository for users, must be changed in future
 */
public class UserRepository extends BaseRepository<User> {

    public UserRepository() {
        // id -1L потом обновится базой данных на нормальный
        create(new User(-1L, "admin", "admin", Role.ADMIN));
        create(new User(-1L, "guest", "guest", Role.GUEST));
        create(new User(-1L, "moderator", "moderator", Role.MODERATOR));
        create(new User(-1L, "user1", "user1", Role.USER));
        create(new User(-1L, "user2", "user2", Role.USER));
        create(new User(-1L, "user3", "user3", Role.USER));
    }

    @Override
    public Stream<User> find(User pattern) {     // будет возвращать пользователей которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(user -> nullOrEquals(pattern.getId(), user.getId()))
                .filter(user -> nullOrEquals(pattern.getLogin(), user.getLogin()))
                .filter(user -> nullOrEquals(pattern.getPassword(), user.getPassword()))
                .filter(user -> nullOrEquals(pattern.getRole(), user.getRole()));
    }

}
