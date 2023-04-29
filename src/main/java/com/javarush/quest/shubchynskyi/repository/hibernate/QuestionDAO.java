package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.Question;
import org.hibernate.SessionFactory;

import java.util.stream.Stream;

public class QuestionDAO extends GenericDAO<Question> {
    public QuestionDAO(SessionFactory sessionFactory) {
        super(Question.class, sessionFactory);
    }

    @Override
    public Stream<Question> find(Question pattern) {
        return null;
    }
}
