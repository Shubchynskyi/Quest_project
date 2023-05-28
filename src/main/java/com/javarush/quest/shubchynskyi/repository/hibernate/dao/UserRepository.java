package com.javarush.quest.shubchynskyi.repository.hibernate.dao;


import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.javarush.quest.shubchynskyi.util.Key.ROLE;

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
        query.setParameter(ROLE, role);
        return query.list();
    }
}
