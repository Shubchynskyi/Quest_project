package com.example.quest_project.service;

import com.example.quest_project.config.Config;
import com.example.quest_project.entity.User;
import com.example.quest_project.repository.Repository;
import com.example.quest_project.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;


/* Сервисы - самая важная часть, тут вся бизнес логика */
/* сейчас сервис просто взаимодействует с репозиторием User-ов */
/* в дальнейшем вся сложная логика будет тут */
public enum UserService {
    USER_SERVICE;
    private final Config config = Config.CONFIG;
//    private final Repository<User> userRepository = new UserRepository();

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
//        Collection<User> users = UserRepository.find(patternUser);
        return config.userRepository.find(patternUser).findAny();
    }
}
