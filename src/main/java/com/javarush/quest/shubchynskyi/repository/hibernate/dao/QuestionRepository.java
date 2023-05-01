package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.hibernate.SessionFactoryCreator;
import org.hibernate.SessionFactory;

public class QuestionRepository extends GenericDAO<Question> {
    public QuestionRepository(SessionFactoryCreator sessionFactoryCreator) {
        super(Question.class, sessionFactoryCreator);
    }
}
