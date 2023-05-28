package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@UtilityClass
public class JavaApplicationConfig {
    private static final ApplicationContext context =
            new AnnotationConfigApplicationContext(ApplicationConfig.class);


    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public static void init() {
        repositoryInit();
    }

    public static void repositoryInit() {
        UserService userService = getBean(UserService.class);

        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().login("user").password("user").role(Role.USER).build());
        }
    }


}