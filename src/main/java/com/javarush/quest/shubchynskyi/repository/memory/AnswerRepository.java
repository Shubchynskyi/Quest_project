package com.javarush.quest.shubchynskyi.repository.memory;

import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.memory.BaseRepository;

import java.util.stream.Stream;

/**
 * repository for Answers, must be changed in future
 */
public class AnswerRepository extends BaseRepository<Answer> {

    @Override
    public Stream<Answer> find(Answer pattern) {
        return map.values()
                .stream()
                .filter(answer -> nullOrEquals(pattern.getId(), answer.getId()))
                .filter(answer -> nullOrEquals(pattern.getQuestionId(), answer.getQuestionId()))
                .filter(answer -> nullOrEquals(pattern.getText(), answer.getText()))
                .filter(answer -> nullOrEquals(pattern.getNextQuestionId(), answer.getNextQuestionId()));
    }

}
