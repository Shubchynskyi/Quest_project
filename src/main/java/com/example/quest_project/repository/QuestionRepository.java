package com.example.quest_project.repository;


import com.example.quest_project.entity.Question;
import com.example.quest_project.repository.BaseRepository;

import java.util.stream.Stream;

/**
 * repository for Questions, must be changed in future
 */
public class QuestionRepository extends BaseRepository<Question> {

//    public QuestionRepository() {
//        // id -1L потом обновится базой данных на нормальный
//        create(new Question(-1L, "admin", "admin", Role.ADMIN));  
//        create(new Question(-1L, "guest", "guest", Role.GUEST));
//        create(new Question(-1L, "moderator", "moderator", Role.MODERATOR));
//        create(new Question(-1L, "Question1", "Question1", Role.Question));
//        create(new Question(-1L, "Question2", "Question2", Role.Question));
//        create(new Question(-1L, "Question3", "Question3", Role.Question));
//    }

    @Override
    public Stream<Question> find(Question pattern) {     // будет возвращать квесты которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(question -> nullOrEquals(pattern.getId(), question.getId()))
                .filter(question -> nullOrEquals(pattern.getQuestId(), question.getQuestId()))
                .filter(question -> nullOrEquals(pattern.getText(), question.getText()))
                .filter(question -> nullOrEquals(pattern.getGameState(), question.getGameState()));
    }

}
