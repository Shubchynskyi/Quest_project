package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.repository.Repository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public abstract class GenericDAO<T extends AbstractEntity> implements Repository<T> {
    private final Class<T> clazz;

    private final SessionFactory sessionFactory;

    public GenericDAO(Class<T> clazz, SessionFactory sessionFactory) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
    }

    public T get(long id) {
        return (T) getCurrentSession().get(clazz, id);
    }

//    public List<T> getItems(int offset, int count) {
//        Query query = getCurrentSession().createQuery("FROM " + clazz.getName(), clazz);
//        query.setFirstResult(offset);
//        query.setMaxResults(count);
//        return query.getResultList();
//    }


    @Override
    public Stream<T> find(T pattern) {
        CriteriaBuilder criteriaBuilder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        Predicate[] predicates = getPredicatesFromPattern(criteriaBuilder, root, pattern);
        if (predicates != null) {
            criteriaQuery.where(predicates);
        }

        return getCurrentSession().createQuery(criteriaQuery).stream();
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
                throw new AppException("Access to filed is deni");
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

    public Collection<T> getAll() {
        String hql = "SELECT t FROM " + clazz.getName() + " t ORDER BY t.id";
        Query<T> query = getCurrentSession().createQuery(hql, clazz);
        return query.list();
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
                throw new AppException("Transaction rollback when try to create");
            }
        }
    }

    public void update(T entity) {
        getCurrentSession().merge(entity);
    }

    public void delete(T entity) {
        getCurrentSession().remove(entity);
    }

    //TODO remove ?
    public void deleteById(long entityId) {
        T entity = get(entityId);
        delete((entity));
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
