package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class UserDbRepository implements Repository<User> {

    private final SessionCreator sessionCreator;

    @Override
    public Collection<User> getAll() {
        return null;
    }

    @Override
    public Stream<User> find(User pattern) {
        return null;
    }

    @Override
    public User get(long id) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                User user = session.get(User.class, id);
                transaction.commit();
                return user;
            } catch (Exception e) {
                transaction.rollback();
                throw new AppException();
            }
        }
    }

    @Override
    public User create(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                session.persist(user);
                transaction.commit();
                return get(user.getId());
            } catch (Exception e) {
                System.err.println("ROLLBACK WHEN CREATE USER");
                transaction.rollback();
            }
        }
        return user;
    }

    @Override
    public void update(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                session.merge(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void delete(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                session.remove(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }
}
