package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordHashingInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.hash-passwords-migration-name}")
    private String migrationName;

    @Override
    public void run(String... args) {

        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM migration_audit WHERE migration_name = ?",
                    Integer.class,
                    migrationName
            );

            if (count != null && count > 0) {
                log.info("Password hashing migration already executed. Skipping...");
                return;
            }

            userRepository.findAll().forEach(user -> {
                if (!user.getPassword().startsWith("$2a$")) {
                    log.info("Hashing password for user with ID: {}", user.getId());
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                }
            });

            jdbcTemplate.update(
                    "INSERT INTO migration_audit (migration_name) VALUES (?)",
                    migrationName
            );

            log.info("Password hashing migration completed successfully.");

        } catch (Exception e) {
            log.error("Error occurred during password hashing migration: {}", e.getMessage(), e);
        }
    }
}