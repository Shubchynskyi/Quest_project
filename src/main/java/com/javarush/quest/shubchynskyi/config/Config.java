package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.entity.user.Role;
import com.javarush.quest.shubchynskyi.entity.user.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import com.javarush.quest.shubchynskyi.util.Key;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config {

    public static void repositoryInit() {
        UserService userService = getBean(UserService.class);

        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().id(-1L).login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().id(-1L).login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().id(-1L).login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().id(-1L).login("user").password("user").role(Role.USER).build());
        }
    }

    private static final Map<Class<?>, Object> beanContainer = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> type) { //QuestService.class
        try {
            if (beanContainer.containsKey(type)) {
                return (T) beanContainer.get(type);
            } else {
                Constructor<?>[] constructors = type.getConstructors();
                Constructor<?> constructor = constructors[0];
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = getBean(parameterTypes[i]);
                }
                Object component = constructor.newInstance(parameters);
                beanContainer.put(type, component);
                return (T) component;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Context broken for " + type, e);
        }
    }


    public static final Path WEB_INF = Paths.get(URI.create(
                    Objects.requireNonNull(
                            Config.class.getResource(Key.REGEX_SLASH_SIGN)
                    ).toString()))
            .getParent();


}
