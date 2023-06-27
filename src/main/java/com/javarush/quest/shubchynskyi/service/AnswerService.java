package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

  // TODO refactoring
    public void create(Answer answer) {
        answerRepository.save(answer);
    }

    @Transactional
    public void update(Answer answer) {
        answerRepository.save(answer);
    }

    @SuppressWarnings("unused")
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }
}
