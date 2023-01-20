package com.example.quest_project.repository;

import com.example.quest_project.entity.Answer;

import java.util.stream.Stream;

/**
 * repository for Answers, must be changed in future
 */
public class AnswerRepository extends BaseRepository<Answer> {

//    public AnswerRepository() {
//        // id -1L потом обновится базой данных на нормальный
//        create(new Answer(-1L, "admin", "admin", Role.ADMIN));  
//        create(new Answer(-1L, "guest", "guest", Role.GUEST));
//        create(new Answer(-1L, "moderator", "moderator", Role.MODERATOR));
//        create(new Answer(-1L, "Answer1", "Answer1", Role.Answer));
//        create(new Answer(-1L, "Answer2", "Answer2", Role.Answer));
//        create(new Answer(-1L, "Answer3", "Answer3", Role.Answer));
//    }

    @Override
    public Stream<Answer> find(Answer pattern) {     // будет возвращать квесты, которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(answer -> nullOrEquals(pattern.getId(), answer.getId()))
                .filter(answer -> nullOrEquals(pattern.getQuestionId(), answer.getQuestionId()))
                .filter(answer -> nullOrEquals(pattern.getText(), answer.getText()))
                .filter(answer -> nullOrEquals(pattern.getNextQuestion(),answer.getNextQuestion()));
    }

}
