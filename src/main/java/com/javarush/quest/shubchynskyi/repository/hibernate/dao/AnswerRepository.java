package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Answer;

public class AnswerRepository extends GenericDAO<Answer> {
    public AnswerRepository(SessionCreator sessionCreator) {
        super(Answer.class, sessionCreator);
    }
}
