package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.game.Answer;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;

public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }


    @SuppressWarnings("unused")
    public void create(Answer answer) {
        answerRepository.create(answer);
    }

    public void update(Answer answer) {
        answerRepository.update(answer);
    }

    @SuppressWarnings("unused")
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }
}
