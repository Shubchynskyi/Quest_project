package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.user.Role;
import com.javarush.quest.shubchynskyi.entity.user.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Client {
    public static void main(String[] args) {
        SessionCreator sessionCreator = new SessionCreator();
        User user = User.builder().id(1L).login("test").password("password").role(Role.USER).build();
        Session session = sessionCreator.getSession();

        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                info(session);
                User dbUser = session.get(User.class, 2L);
                dbUser = session.get(User.class, 5L);
                info(session);
                dbUser.setPassword("hyfukgkl");
//                session.persist(user);
                info(session);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
        System.out.println(user);


    }

    private static void info(Session session) {
        System.out.println("-".repeat(100));
        System.out.printf("Dirty: %s%n, Stat: %s%n, Session: %s%n",
                session.isDirty(), session.getStatistics(), session);
        System.out.println("-".repeat(100));
    }
}
