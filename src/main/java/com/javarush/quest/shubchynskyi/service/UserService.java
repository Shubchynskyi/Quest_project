package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserRepository userRepository;

    public User build(String userLogin, String userPassword, String userRole) {
        return User.builder()
                .login(userLogin)
                .password(userPassword)
                .role(Role.valueOf(userRole))
                .build();
    }

    public User build(String userId, String userLogin, String userPassword, String userRole) {
        return User.builder()
                .id(Long.valueOf(userId))
                .login(userLogin)
                .password(userPassword)
                .role(Role.valueOf(userRole))
                .build();
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

    public Collection<User> getAll() {
        return userRepository.findAll();
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
