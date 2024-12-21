package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;
    private final ImageService imageService;


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
    public void delete(Question question) {
        imageService.deleteOldFiles(question.getImage());
        Collection<Answer> answers = question.getAnswers();
        answerService.deleteAll(answers);
        questionRepository.delete(question);
    }
}
