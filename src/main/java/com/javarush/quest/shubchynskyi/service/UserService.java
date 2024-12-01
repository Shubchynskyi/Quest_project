package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.javarush.quest.shubchynskyi.constant.Key.USER_NOT_FOUND_WITH_ID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isLoginExist(String login) {
        Example<User> user = Example.of(
                User.builder()
                        .login(login)
                        .build()
        );
        return userRepository.exists(user);
    }

    @Transactional
    public Optional<User> create(User user) {
        String hashedUserPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedUserPassword);
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }

    @Transactional
    public Optional<User> update(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_ID + user.getId()));

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            String hashedUserPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(hashedUserPassword);
        }

        existingUser.setLogin(user.getLogin());
        existingUser.setRole(user.getRole());

        User updatedUser = userRepository.save(existingUser);
        return Optional.of(updatedUser);
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

    public Optional<User> get(String login, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByLogin(login);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}