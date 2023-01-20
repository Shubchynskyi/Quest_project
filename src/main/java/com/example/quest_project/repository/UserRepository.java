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
public class UserRepository implements Repository<User> {
    private final Map<Long, User> map = new HashMap<>(); // map for all users
    public static final AtomicLong id = new AtomicLong(System.currentTimeMillis()); // для чего System.currentTimeMillis()

    public UserRepository() {
        map.put(1L, new User(1L, "admin", "admin", Role.ADMIN));
        map.put(2L, new User(2L, "guest", "guest", Role.GUEST));
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
    public Stream<User> find(User pattern) {     // будет возвращать пользователей которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(user -> nullOrEquals(pattern.getId(), user.getId()))
                .filter(user -> nullOrEquals(pattern.getLogin(), user.getLogin()))
                .filter(user -> nullOrEquals(pattern.getPassword(), user.getPassword()))
                .filter(user -> nullOrEquals(pattern.getRole(), user.getRole()));
    }

    // если в паттерне null, то это поле пропускаем (не ложится в коллекцию метода find)
    private boolean nullOrEquals(Object patternField, Object repoField) {
        return patternField == null || patternField.equals(repoField);
    }

    @Override
    public User get(long id) {
        return map.get(id);
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
