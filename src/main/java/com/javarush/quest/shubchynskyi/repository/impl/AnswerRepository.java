package com.javarush.quest.shubchynskyi.repository.impl;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Answer;
import org.springframework.stereotype.Repository;

@Repository
public class AnswerRepository extends GenericDAO<Answer> {
    public AnswerRepository(SessionCreator sessionCreator) {
        super(Answer.class, sessionCreator);
    }
}
