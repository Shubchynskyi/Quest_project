package com.example.quest_project;

import com.example.quest_project.entity.User;
import com.example.quest_project.service.UserService;

import java.util.Collection;

public class Runner {
    private static UserService userService = UserService.USER_SERVICE;

    public static void main(String[] args) {
        Collection<User> users = userService.getAll();
        System.out.println(users);
    }
}
