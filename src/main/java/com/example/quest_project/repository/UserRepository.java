package com.example.quest_project.repository;

import com.example.quest_project.entity.Role;
import com.example.quest_project.entity.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * repository for users, must be changed in future
 */
public class UserRepository implements Repository<User> {
    private final Map<Long, User> map = new HashMap<>(); // map for all users
    public static final AtomicLong id = new AtomicLong(System.currentTimeMillis()); // для чего System.currentTimeMillis()

    public UserRepository() {
        map.put(1L, new User(1L, "admin", "admin", Role.ADMIN));
        map.put(2L, new User(2L, "guest", "", Role.GUEST));
        map.put(3L, new User(3L, "moderator", "moderator", Role.MODERATOR));
        map.put(4L, new User(4L, "user1", "user1", Role.USER));
        map.put(5L, new User(5L, "user2", "user2", Role.USER));
        map.put(6L, new User(6L, "user3", "user3", Role.USER));
    }

    @Override
    public Collection<User> getAll() { // возвращает всех пользователей
        return map.values();
    }

    @Override
    public Optional<User> get(long id) {         // почему Optional?
        return Optional.ofNullable(map.get(id)); // вернет конкретного пользователя
    }

    @Override
    public void create(User entity) {       // создаст нового пользователя
        entity.setId(id.incrementAndGet());
        update(entity);
    }

    @Override
    public void update(User entity) {       // заменяет пользователя
        map.put(entity.getId(), entity);
    }

    @Override
    public void delete(User entity) {       // удаляет пользователя
        map.remove(entity.getId());
    }

}
