package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Question;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepository extends GenericDAO<Question> {
    public QuestionRepository(SessionCreator sessionCreator) {
        super(Question.class, sessionCreator);
    }
}