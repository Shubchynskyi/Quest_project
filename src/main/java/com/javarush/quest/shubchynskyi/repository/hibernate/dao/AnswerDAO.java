package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Answer;
import org.hibernate.SessionFactory;

public class AnswerDAO extends GenericDAO<Answer> {
    public AnswerDAO(SessionFactory sessionFactory) {
        super(Answer.class, sessionFactory);
    }
}
