package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.repository.Repository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public abstract class GenericDAO<T extends AbstractEntity> implements Repository<T> {
    private final Class<T> clazz;
    final SessionCreator sessionCreator;

    public GenericDAO(Class<T> clazz, SessionCreator sessionCreator) {
        this.clazz = clazz;
        this.sessionCreator = sessionCreator;
    }

    public T get(long id) {
        return sessionCreator.getSession().get(clazz, id);
    }

    public Collection<T> getAll() {
        Session session = sessionCreator.getSession();
        String hql = "SELECT t FROM " + clazz.getName() + " t ORDER BY t.id";
        return session.createQuery(hql, clazz).list();
    }


    public void create(T entity) {
        Session session = sessionCreator.getSession();
//        try(session) {
//            Transaction transaction = session.beginTransaction();
//            try {
        session.persist(entity);
//        return get(entity.getId());

//                transaction.commit();
//            } catch (Exception e) {
//                transaction.rollback();
//                throw new RuntimeException(e);
//            }
//        }

    }

    public void update(T entity) {
        sessionCreator.getSession().merge(entity);
    }

    public void delete(T entity) {
        sessionCreator.getSession().remove(entity);
    }

    @Override
    public Stream<T> find(T pattern) {
        CriteriaBuilder criteriaBuilder = sessionCreator.getSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        Predicate[] predicates = getPredicatesFromPattern(criteriaBuilder, root, pattern);
        if (predicates != null) {
            criteriaQuery.where(predicates);
        }
        List<T> genericlist = sessionCreator.getSession().createQuery(criteriaQuery).list();
        return genericlist.stream();
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
        return field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(OneToMany.class);
    }

}
