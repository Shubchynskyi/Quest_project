package com.example.quest_project.service;

import com.example.quest_project.config.Config;
import com.example.quest_project.entity.Answer;
import com.example.quest_project.entity.Question;

import java.util.Optional;

public enum QuestionService {

    QUESTION_SERVICE;

    private final Config config = Config.CONFIG;

    public Optional<Question> get(Long id) {
        return config.questionRepository.find(Question.builder().id(id).build()).findAny(); //TODO переместить
    }

    public Optional<Question> get(String id) {
        return config.questionRepository.find(Question.builder().id(Long.valueOf(id)).build()).findAny(); //TODO переместить
    }

    // передаю id ответа и получаю следующий вопрос
//    public Optional<Question> getNextQuestionOptional(String answerId) {
//        Long nextQuestionId = config.answerRepository.get(Long.parseLong(answerId)).getNextQuestionId();
//        return Optional.ofNullable(config.questionRepository.get(nextQuestionId));
//    }

    public void create(Question question) {
        config.questionRepository.create(question);
    }


    //TODO переделать, получаю вопрос по id и делаю в нем новый текст или новую картинку
    public void update(Long id, String text, Question question) {

        config.questionRepository.update(question);
    }


    //TODO удалить вопрос по id и все связанные с ним вопросы
    // нужен remove метод, который заходит в вопрос, если лист ответов пустой, то удаляет
    // если есть ответы, то вызывает метод remove у ответа, в котором вызывается remove у вопроса к которому ведет ответ
    public void delete(Long id, Question question) {
        config.questionRepository.delete(question);
    }
}
