package com.javarush.quest.shubchynskyi.repository.hibernate.dao;


import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

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
}
