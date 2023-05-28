package com.javarush.quest.shubchynskyi.config.bpp;


import com.javarush.quest.shubchynskyi.config.JavaApplicationConfig;
import com.javarush.quest.shubchynskyi.config.SessionCreator;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransactionalAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Map<String, Class<?>> map = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        if (aClass.isAnnotationPresent(Transactional.class) || methodIsAnnotationPresent(aClass)) {
            map.put(beanName, aClass);
        }
        return bean;
    }

    private boolean methodIsAnnotationPresent(Class<?> aClass) {
        return Arrays.stream(aClass.getMethods())
                .anyMatch(m -> m.isAnnotationPresent(Transactional.class));
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> aClass = map.get(beanName);
        if (aClass != null) {
            bean = proxy(bean, aClass);
            System.out.printf("bean in Tx proxy! (%s)\n", beanName);
        }
        return bean;
    }

    private Object proxy(Object beanOrProxy, Class<?> beanRealClass) {
        MethodInterceptor handler = (obj, method, args, proxy) -> {
            Object result;
            if (method.isAnnotationPresent(Transactional.class)) {
                SessionCreator sessionCreator = JavaApplicationConfig.getBean(SessionCreator.class);
                sessionCreator.beginTransactional();
                try {
                    result = proxy.invoke(beanOrProxy, args);
                } finally {
                    sessionCreator.endTransactional();
                }
            } else {
                result = proxy.invoke(beanOrProxy, args);
            }
            return result;
        };
        return Enhancer.create(beanRealClass, handler);
    }
}
