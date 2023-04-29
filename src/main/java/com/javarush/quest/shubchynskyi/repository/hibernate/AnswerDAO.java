package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.Answer;
import org.hibernate.SessionFactory;

import java.util.stream.Stream;

public class AnswerDAO extends GenericDAO<Answer> {
    public AnswerDAO(SessionFactory sessionFactory) {
        super(Answer.class, sessionFactory);
    }

    @Override
    public Stream<Answer> find(Answer pattern) {
        return null;
    }
}
