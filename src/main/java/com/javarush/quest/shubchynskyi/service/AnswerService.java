package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.AnswerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    @Autowired
    public void setAnswerRepository(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    private AnswerRepository answerRepository;




    @SuppressWarnings("unused")
    public void create(Answer answer) {
        answerRepository.create(answer);
    }

    @Transactional
    public void update(Answer answer) {
        answerRepository.update(answer);
    }

    @SuppressWarnings("unused")
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }
}
