package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.*;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.UserRepository;
import org.hibernate.Session;

import java.util.List;
import java.util.stream.Stream;

public class Client {
    public static void main(String[] args) {
        SessionFactoryCreator sessionFactoryCreator = new SessionFactoryCreator();
        User user = User.builder().login("test").password("password").role(Role.USER).build();
        User pattern = User.builder().login("test").build();
//        Session session = sessionFactoryCreator.getSessionFactory().openSession();

        // TODO добавить транзакции всем методам в DAO
        // задать вопрос по сессиям

        UserRepository userRepository = new UserRepository(sessionFactoryCreator.getSessionFactory());
        userRepository.create(user);
        Stream<User> userStream = userRepository.find(pattern);
        List<User> users = userStream.toList();
        for (User user1 : users) {
            System.out.println(user1);
        }

//        Collection<User> userCollection = new UserDAO(sessionFactoryCreator.getSessionFactory()).getAll();
//        userCollection.forEach(System.out::println);


//        try (session) {
//            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
//            try {
//                UserDAO userDbRepository = new UserDAO(sessionFactoryCreator.getSessionFactory());
//
//                transaction.commit();
//            } catch (Exception e) {
//                transaction.rollback();
//            }
//        }
//        System.out.println(user);





    }

    private static void info(Session session) {
        System.out.println("-".repeat(100));
        System.out.printf("Dirty: %s%n, Stat: %s%n, Session: %s%n",
                session.isDirty(), session.getStatistics(), session);
        System.out.println("-".repeat(100));
    }
}
