package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.game.Question;
import com.javarush.quest.shubchynskyi.config.Config;

import java.util.Optional;

public enum QuestionService {

    QUESTION_SERVICE;

    private final Config config = Config.CONFIG;

    @SuppressWarnings("unused")
    public Optional<Question> get(Long id) {
        return config.questionRepository.find(Question.builder().id(id).build()).findAny();
    }

    public Optional<Question> get(String id) {
        return config.questionRepository.find(Question.builder().id(Long.valueOf(id)).build()).findAny();
    }

    public void create(Question question) {
        config.questionRepository.create(question);
    }

    public void update(Question question) {
        config.questionRepository.update(question);
    }

    @SuppressWarnings("unused")
    public void delete(Question question) {
        config.questionRepository.delete(question);
    }
}
