package com.javarush.quest.shubchynskyi;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@SpringBootApplication
public class App {

    @Bean
    public Lock lock() {
        return new ReentrantLock();
    }

    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);

        UserService userService = context.getBean(UserService.class);
        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().login("user").password("user").role(Role.USER).build());
        }
    }
}
