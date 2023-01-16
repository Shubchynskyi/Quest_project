package com.example.quest_project.service;

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
    private final Repository<User> userRepository = new UserRepository();

    public void create(User user) {
        userRepository.create(user);
    }

    public void update(User user) {
        userRepository.update(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    public Optional<User> get(long id) {
        return userRepository.get(id);
    }
}
