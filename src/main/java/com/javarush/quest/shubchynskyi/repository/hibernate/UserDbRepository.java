package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.repository.abstract_repo.Repository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class UserDbRepository implements Repository<User> {

    private final SessionFactoryCreator sessionFactoryCreator;

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
        Session session = sessionFactoryCreator.getSessionFactory().openSession();
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
    public void create(User user) {
        Session session = sessionFactoryCreator.getSessionFactory().openSession();;
        try (session) {
            Transaction transaction = session.beginTransaction(); // работу с гибером всегда надо начинать с транзакции
            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void update(User user) {
        Session session = sessionFactoryCreator.getSessionFactory().openSession();
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
        Session session = sessionFactoryCreator.getSessionFactory().openSession();
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
