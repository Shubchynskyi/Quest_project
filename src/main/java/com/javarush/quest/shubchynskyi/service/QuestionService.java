package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.QuestionRepository;
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
        return questionRepository.findById(id);
    }

    public Optional<Question> get(String id) {
        return get(Long.valueOf(id));
    }

    @Transactional
    public void create(Question question) {
        questionRepository.save(question);
    }

    @Transactional
    public void update(Question question) {
        questionRepository.save(question);
    }

    @Transactional
    @SuppressWarnings("unused")
    public void delete(Question question) {
        questionRepository.delete(question);
    }
}
