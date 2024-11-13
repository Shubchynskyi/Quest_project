package com.javarush.quest.shubchynskyi;

import com.javarush.quest.shubchynskyi.config.ImageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ImageProperties.class)
public class App {

    @Bean
    public Lock lock() {
        return new ReentrantLock();
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

    }
}