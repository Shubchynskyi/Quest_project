package com.javarush.quest.shubchynskyi;

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
        SpringApplication.run(App.class, args);

    }
}
