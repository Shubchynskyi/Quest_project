package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Answer;
import org.hibernate.SessionFactory;

public class AnswerRepository extends GenericDAO<Answer> {
    public AnswerRepository(SessionFactory sessionFactory) {
        super(Answer.class, sessionFactory);
    }
}
