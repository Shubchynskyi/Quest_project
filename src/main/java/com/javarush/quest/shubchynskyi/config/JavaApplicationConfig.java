package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.config.aspects.LoggerAspect;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@UtilityClass
public class JavaApplicationConfig {
    private static final ApplicationContext context =
            new AnnotationConfigApplicationContext(ApplicationConfig.class);

//    public final static Path CLASSES_ROOT = Paths.get(URI.create(
//            Objects.requireNonNull(
//                    ApplicationProperties.class.getResource("/")
//            ).toString()));
//
//    //only in Tomcat (not use in tests)
//    public final static Path WEB_INF = CLASSES_ROOT.getParent();
//    public static final Path WEB_INF =
//            Paths.get(URI.create(Objects.requireNonNull(
//                    JavaApplicationConfig.class.getResource(Key.REGEX_SLASH_SIGN)).toString())).getParent();


    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public static void init() {
        String[] names = context.getBeanDefinitionNames();
        System.out.println("============= context =============");
        Arrays.asList(names).forEach(System.out::println);
        System.out.println("============= context =============");
        repositoryInit();
    }
//    private final Map<Class<?>, Object> beanContainer = new HashMap<>();

//    @SuppressWarnings("unchecked")
//    public <T> T getBean(Class<T> type) { //QuestService.class
//        try {
//            if (beanContainer.containsKey(type)) {
//                return (T) beanContainer.get(type);
//            } else {
//                Constructor<?>[] constructors = type.getConstructors();
//                Constructor<?> constructor = constructors[0];
//                Class<?>[] parameterTypes = constructor.getParameterTypes();
//                Object[] parameters = new Object[parameterTypes.length];
//                for (int i = 0; i < parameters.length; i++) {
//                    parameters[i] = getBean(parameterTypes[i]);
//                }
//                Object component = checkTransactional(type)
//                        ? constructProxyInstance(type, parameterTypes, parameters)
//                        : constructor.newInstance(parameters);
//                beanContainer.put(type, component);
//                return (T) component;
//            }
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException("Context broken for " + type, e);
//        }
//    }

    //    private <T> boolean checkTransactional(Class<T> type) {
//        return type.isAnnotationPresent(Transactional.class)
//               || Arrays.stream(type.getMethods())
//                       .anyMatch(method -> method.isAnnotationPresent(Transactional.class));
//    }
//
//    @SneakyThrows
//    private Object constructProxyInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {
//        Class<?> proxy = new ByteBuddy()
//                .subclass(type)
//                .method(isDeclaredBy(ElementMatchers.isAnnotatedWith(Transactional.class))
//                        .or(ElementMatchers.isAnnotatedWith(Transactional.class)))
//                .intercept(MethodDelegation.to(Interceptor.class))
//                .make()
//                .load(type.getClassLoader())
//                .getLoaded();
//        Constructor<?> constructor = proxy.getConstructor(parameterTypes);
//        return constructor.newInstance(parameters);
//    }
//
//    @LoggerAspect
    public static void repositoryInit() {
        UserService userService = getBean(UserService.class);

        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().login("user").password("user").role(Role.USER).build());
        }
    }


//    public class Interceptor {
//        @RuntimeType
//        public static Object intercept(@This Object self,
//                                       @Origin Method method,
//                                       @AllArguments Object[] args,
//                                       @SuperMethod Method superMethod) throws Throwable {
//            getBean(SessionCreator.class).beginTransactional();
//            try {
//                return superMethod.invoke(self, args);
//            } finally {
//                getBean(SessionCreator.class).endTransactional();
//            }
//        }
//    }
}
