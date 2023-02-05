package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.config.Config;
import com.javarush.quest.shubchynskyi.entity.user.User;

import java.util.Collection;
import java.util.Optional;


public enum UserService {
    USER_SERVICE;
    private final Config config = Config.CONFIG;

    public void create(User user) {
        config.userRepository.create(user);
    }

    public void update(User user) {
        config.userRepository.update(user);
    }

    public void delete(User user) {
        config.userRepository.delete(user);
    }

    public Collection<User> getAll() {
        return config.userRepository.getAll();
    }

    public Optional<User> get(long id) {
        return Optional.ofNullable(config.userRepository.get(id));
    }

    public Optional<User> get(String login, String password) {
        User patternUser = User
                .builder()
                .login(login)
                .password(password)
                .build();
        return config.userRepository.find(patternUser).findAny();
    }
}
