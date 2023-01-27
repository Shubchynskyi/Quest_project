package com.example.quest_project.service;

import com.example.quest_project.config.Config;
import com.example.quest_project.entity.Answer;
import com.example.quest_project.entity.Question;

public enum AnswerService {

    ANSWER_SERVICE;

    private final Config config = Config.CONFIG;

    public void create(Answer answer) {
        config.answerRepository.create(answer);
    }

    public void update(Answer answer) {
        config.answerRepository.update(answer);
    }

    public void delete(Answer answer) {
        config.answerRepository.delete(answer);
    }
}
