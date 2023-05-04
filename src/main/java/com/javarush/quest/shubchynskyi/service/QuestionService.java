package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.QuestionRepository;
import jakarta.transaction.Transactional;


import java.util.Optional;

public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @SuppressWarnings("unused")
    public Optional<Question> get(Long id) {
        return questionRepository.find(Question.builder().id(id).build()).findAny();
    }
    @Transactional
    public Optional<Question> get(String id) {
        return questionRepository.find(Question.builder().id(Long.valueOf(id)).build()).findAny();
    }
    @Transactional
    public void create(Question question) {
        questionRepository.create(question);
    }
    @Transactional
    public void update(Question question) {
        questionRepository.update(question);
    }

    @SuppressWarnings("unused")
    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
