package com.javarush.quest.shubchynskyi;

import com.javarush.quest.shubchynskyi.config.JavaApplicationConfig;
import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("check check");
        JavaApplicationConfig.init();
        UserService userService = JavaApplicationConfig.getBean(UserService.class);

        User build = User.builder().login("testMai3").password("testPass").role(Role.ADMIN).build();
        userService.create(build);

        Optional<User> user = userService.get(5);

        Optional<User> user1 = userService.get("testMain1", "testPass");

        if(user.isPresent() && user1.isPresent()) {
            System.out.println(user.get());
            System.out.println(user1.get());
        }

//        System.err.println("TETввыыыыыыыыыв!!!!!!!!!!!!!!!!");
//        System.err.println("TEST!!!!!!!!!!!!!!!!");
//        System.err.println("TEST!!!!!!!!!!!!!!!!");
//        System.err.println("TEST!!!!!!!!!!!!!!!!");
    }
}
