package com.javarush.quest.shubchynskyi.repository.hibernate.dao;


import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.DaoException;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserDAO extends GenericDAO<User> {
    public UserDAO(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    @SuppressWarnings("unused")
    public List<User> getUsersByRole(Role role) {
        String hql = """
                SELECT u FROM User u
                WHERE u.role = :role
                """;
        Query<User> query = getCurrentSession().createQuery(hql, User.class);
        query.setParameter("role", role);
        return query.list();
    }


//    @Override
    public Stream<User> find1(User pattern) {
        Session session = super.getCurrentSession();
        try (session){
            Transaction tx = session.beginTransaction();
            try {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
                Root<User> userRoot = criteriaQuery.from(User.class);
                List<Predicate> predicates=new ArrayList<>();
                for (Field field : pattern.getClass().getDeclaredFields()) {
                    field.trySetAccessible();
                    Object value = field.get(pattern);
                    String name = field.getName();
                    if (!field.isAnnotationPresent(Transient.class) &&
                        !field.isAnnotationPresent(ManyToOne.class) &&
                        !field.isAnnotationPresent(OneToMany.class) &&
                        !field.isAnnotationPresent(OneToOne.class) &&
                        !field.isAnnotationPresent(ManyToMany.class)
                        && value != null){
                        Predicate predicate = criteriaBuilder.equal(userRoot.get(name), value);
                        predicates.add(predicate);
                    }
                }
                criteriaQuery.select(userRoot).where(predicates.toArray(Predicate[]::new));
                List<User> userList = session.createQuery(criteriaQuery).list();
                tx.commit();
                return userList.stream();
            } catch (Exception e){
                tx.rollback();
                throw new DaoException(e);
            }
        }
    }

}
