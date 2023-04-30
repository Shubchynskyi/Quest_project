package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Question;
import org.hibernate.SessionFactory;

public class QuestionDAO extends GenericDAO<Question> {
    public QuestionDAO(SessionFactory sessionFactory) {
        super(Question.class, sessionFactory);
    }
}
