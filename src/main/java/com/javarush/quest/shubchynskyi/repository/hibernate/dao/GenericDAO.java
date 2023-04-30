package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.exception.DaoException;
import com.javarush.quest.shubchynskyi.repository.Repository;
import com.javarush.quest.shubchynskyi.util.Log;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class GenericDAO<T extends AbstractEntity> implements Repository<T> {
    private final Class<T> clazz;
    private final SessionFactory sessionFactory;

    public GenericDAO(Class<T> clazz, SessionFactory sessionFactory) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
    }

    public T get(long id) {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = getCurrentSession().get(clazz, id);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        clazz.getSimpleName()));
                throw new DaoException();
            }
        }
    }

//    public List<T> getItems(int offset, int count) {
//        Query query = getCurrentSession().createQuery("FROM " + clazz.getName(), clazz);
//        query.setFirstResult(offset);
//        query.setMaxResults(count);
//        return query.getResultList();
//    }


    public Collection<T> getAll() {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                String hql = "SELECT t FROM " + clazz.getName() + " t ORDER BY t.id";
                List<T> result = getCurrentSession().createQuery(hql, clazz).list();
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        clazz.getSimpleName()));
                throw new DaoException();
            }
        }
    }

    public void create(T entity) {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(entity);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        entity.getClass().getSimpleName()));
            }
        }
    }

    public void update(T entity) {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(entity);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        entity.getClass().getSimpleName()));
            }
        }
    }

    public void delete(T entity) {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                getCurrentSession().remove(entity);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        entity.getClass().getSimpleName()));
            }
        }
    }

    @Override
    public Stream<T> find(T pattern) {
        Session session = getCurrentSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();
                CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
                Root<T> root = criteriaQuery.from(clazz);

                Predicate[] predicates = getPredicatesFromPattern(criteriaBuilder, root, pattern);
                if (predicates != null) {
                    criteriaQuery.where(predicates);
                }
                Stream<T> result = getCurrentSession().createQuery(criteriaQuery).stream();
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                log.warn(String.format(Log.ROLLBACK_MESSAGE_PATTERN,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        clazz.getSimpleName()));
                throw new DaoException();
            }
        }
    }

    private Predicate[] getPredicatesFromPattern(CriteriaBuilder criteriaBuilder, Root<T> root, T pattern) {
        List<Predicate> predicates = new ArrayList<>();

        for (Field field : pattern.getClass().getDeclaredFields()) {
            if (isIgnoredField(field)) {
                continue;
            }

            try {
                field.trySetAccessible();
                Object fieldValue = field.get(pattern);
                if (fieldValue != null) {
                    Path<?> fieldPath = root.get(field.getName());
                    predicates.add(criteriaBuilder.equal(fieldPath, fieldValue));
                }
            } catch (IllegalAccessException e) {
                throw new AppException("Access to field is deni");
            }
        }

        return predicates.isEmpty() ? null : predicates.toArray(new Predicate[0]);
    }

    private boolean isIgnoredField(Field field) {
        return field.isAnnotationPresent(Transient.class) ||
               field.isAnnotationPresent(ManyToOne.class) ||
               field.isAnnotationPresent(ManyToMany.class) ||
               field.isAnnotationPresent(OneToOne.class) ||
               field.isAnnotationPresent(OneToMany.class);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
