package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Question;
import org.hibernate.SessionFactory;

public class QuestionRepository extends GenericDAO<Question> {
    public QuestionRepository(SessionFactory sessionFactory) {
        super(Question.class, sessionFactory);
    }
}
