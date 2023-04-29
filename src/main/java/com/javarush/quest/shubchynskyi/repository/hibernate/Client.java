package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Client {
    public static void main(String[] args) {
        SessionFactoryCreator sessionFactoryCreator = new SessionFactoryCreator();
        User user = User.builder().login("test").password("password").role(Role.USER).build();
        Session session = sessionFactoryCreator.getSessionFactory().openSession();

        List<Answer> fromAnswer = session.createQuery("FROM Answer", Answer.class).list();
        List<Game> fromGame = session.createQuery("FROM Game", Game.class).list();
        List<Quest> fromQuest = session.createQuery("FROM Quest", Quest.class).list();
        List<Question> fromQuestion = session.createQuery("FROM Question", Question.class).list();
        List<User> fromUser = session.createQuery("FROM User", User.class).list();

        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                UserDAO userDbRepository = new UserDAO(sessionFactoryCreator.getSessionFactory());
                userDbRepository.create(user);
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
