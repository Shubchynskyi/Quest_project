package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.hibernate.SessionFactoryCreator;
import org.hibernate.SessionFactory;

public class AnswerRepository extends GenericDAO<Answer> {
    public AnswerRepository(SessionFactoryCreator sessionFactoryCreator) {
        super(Answer.class, sessionFactoryCreator);
    }
}
