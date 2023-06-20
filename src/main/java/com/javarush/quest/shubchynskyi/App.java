package com.javarush.quest.shubchynskyi;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;



@SpringBootApplication
public class App {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public static void main(String[] args) {
        var context = SpringApplication.run(App.class, args);

        UserService userService = context.getBean(UserService.class);
        if (userService.get(1L).isEmpty()) {
            userService.create(User.builder().login("admin").password("admin").role(Role.ADMIN).build());
            userService.create(User.builder().login("guest").password("guest").role(Role.GUEST).build());
            userService.create(User.builder().login("moderator").password("moderator").role(Role.MODERATOR).build());
            userService.create(User.builder().login("user").password("user").role(Role.USER).build());
        }




//        JavaApplicationConfig.init();
//        UserService userService = JavaApplicationConfig.getBean(UserService.class);
//
//        User build = User.builder().login("test55").password("testPass").role(Role.ADMIN).build();
//        userService.create(build);
//
//        Optional<User> user = userService.get(5);
//
//        Optional<User> user1 = userService.get("test7", "testPass");
//
//        if (user.isPresent() && user1.isPresent()) {
//            System.out.println(user.get());
//            System.out.println(user1.get());
//        }

    }

//    //any runtime
//    public final static Path CLASSES_ROOT = Paths.get(URI.create(
//            Objects.requireNonNull(
//                    ApplicationProperties.class.getResource("/")
//            ).toString()));
//
//    //only in Tomcat (not use in tests)
//    public final static Path WEB_INF = CLASSES_ROOT.getParent();
}
