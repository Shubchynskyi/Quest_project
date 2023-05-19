package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    public void setQuestionRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    private QuestionRepository questionRepository;


    @SuppressWarnings("unused")
    public Optional<Question> get(Long id) {
        return questionRepository.find(Question.builder().id(id).build()).findAny();
    }

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

    @Transactional
    @SuppressWarnings("unused")
    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
