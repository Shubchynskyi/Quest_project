package com.javarush.quest.shubchynskyi.config.old;

import com.javarush.quest.shubchynskyi.config.old.ApplicationProperties;
import com.javarush.quest.shubchynskyi.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

//@Component
public class SessionCreator implements AutoCloseable {

    private final SessionFactory sessionFactory;
    private final ThreadLocal<AtomicInteger> levelBox = new ThreadLocal<>();
    private final ThreadLocal<Session> sessionBox = new ThreadLocal<>();

    public SessionCreator(ApplicationProperties applicationProperties) {
        Configuration configuration = new Configuration();
        configuration.setProperties(applicationProperties);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Quest.class);
        configuration.addAnnotatedClass(Question.class);
        configuration.addAnnotatedClass(Answer.class);
        configuration.addAnnotatedClass(Game.class);
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        sessionFactory = configuration.buildSessionFactory();
    }

    public Session getSession() {
        return sessionBox.get() == null || !sessionBox.get().isOpen()
                ? sessionFactory.openSession()
                : sessionBox.get();
    }

    public void beginTransactional() {
        if (levelBox.get() == null) {
            levelBox.set(new AtomicInteger(0));
        }
        AtomicInteger level = levelBox.get();
        if (level.getAndIncrement() == 0) {
            Session session = getSession();
            sessionBox.set(session);
            session.beginTransaction();
        }
        log(level.get(), "begin level: ");
    }


    public void endTransactional() {
        AtomicInteger level = levelBox.get();
        Session session = sessionBox.get();
        log(level.get(), "end level: ");
        if (level.decrementAndGet() == 0) {
            try {
                session.getTransaction().commit();
            } catch (RuntimeException e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    private void log(int level, String message) {
        String simpleName = Thread.currentThread().getStackTrace()[4].toString();
        System.out.println("\t".repeat(level) + message + level + " from " + simpleName);
        System.out.flush();
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}

//    private final SessionFactory sessionFactory;
//    private final Session session;
//
//    public SessionCreator(ApplicationProperties applicationProperties) {
//        Configuration configuration = new Configuration();
//        configuration.setProperties(applicationProperties);
//        configuration.addAnnotatedClass(User.class);
//        configuration.addAnnotatedClass(Quest.class);
//        configuration.addAnnotatedClass(Question.class);
//        configuration.addAnnotatedClass(Answer.class);
//        configuration.addAnnotatedClass(Game.class);
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
//        sessionFactory = configuration.buildSessionFactory();
//        session = sessionFactory.openSession();
//    }
//
//    public Session getSession() {
//        return session == null || !session.isOpen()
//                ? sessionFactory.openSession()
//                : session;
//    }
//
//
//
//
//    @Override
//    public void close() {
//        sessionFactory.close();
//    }
//}

