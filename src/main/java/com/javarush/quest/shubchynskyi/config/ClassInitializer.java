package com.javarush.quest.shubchynskyi.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ClassInitializer {

    private final Map<Class<?>, Object> beanContainer = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) { //QuestService.class
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
}
