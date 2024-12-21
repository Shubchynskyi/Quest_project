package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Optional<Answer> get(Long id) {
        return answerRepository.findById(id);
    }

    public void create(Answer answer) {
        answerRepository.save(answer);
    }

    @Transactional
    public void update(Answer answer) {
        answerRepository.save(answer);
    }

    @Transactional
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Transactional
    public void deleteAll(Collection<Answer> answers) {
        answerRepository.deleteAll(answers);
    }
}