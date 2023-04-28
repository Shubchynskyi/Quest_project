package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@UtilityClass
public class Config {

    public static void repositoryInit() {
        UserService userService = ClassInitializer.getBean(UserService.class);

        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().id(-1L).login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().id(-1L).login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().id(-1L).login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().id(-1L).login("user").password("user").role(Role.USER).build());
        }
    }

    public final Path WEB_INF = Paths.get(URI.create(Objects.requireNonNull(Config.class.getResource(Key.REGEX_SLASH_SIGN)).toString())).getParent();

}
