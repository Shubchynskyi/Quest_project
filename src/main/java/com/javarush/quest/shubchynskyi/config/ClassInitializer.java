package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.exception.AppException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ClassInitializer {

    private final Map<Class<?>, Object> beanContainer = new HashMap<>();



    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
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
            throw new AppException("Context broken for " + type, e);
        }
    }
}
