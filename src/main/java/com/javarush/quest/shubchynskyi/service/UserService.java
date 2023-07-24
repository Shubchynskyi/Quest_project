package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isLoginExist(String login) {
        Example<User> user = Example.of(User.builder().login(login).build());
        return userRepository.exists(user);
    }

    @Transactional
    public Optional<User> create(User user) {
        userRepository.save(user);
        return userRepository.findAll(Example.of(user)).stream().findAny();
    }

    @Transactional
    public void update(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    public Collection<Optional<User>> getAll() {
        return userRepository.findAll().stream()
                .map(Optional::of)
                .collect(Collectors.toList());
    }

    public Optional<User> get(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> get(String login, String password) {
        User patternUser = User
                .builder()
                .login(login)
                .password(password)
                .build();
        return userRepository.findAll(Example.of(patternUser)).stream().findAny();
    }

    @Override
    public String toString() {
        return "UserService";
    }
}
