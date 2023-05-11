package com.javarush.quest.shubchynskyi.repository.hibernate.dao;


import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserRepository extends GenericDAO<User> {
    public UserRepository(SessionCreator sessionCreator) {
        super(User.class, sessionCreator);
    }

    @SuppressWarnings("unused")
    public List<User> getUsersByRole(Role role) {
        String hql = """
                SELECT u FROM User u
                WHERE u.role = :role
                """;
        Query<User> query = sessionCreator.getSession().createQuery(hql, User.class);
        query.setParameter("role", role);
        return query.list();
    }
}
