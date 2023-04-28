package com.javarush.quest.shubchynskyi.repository;


import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.abstract_repo.BaseRepository;

import java.util.stream.Stream;

/**
 * repository for Questions, must be changed in future
 */
public class QuestionRepository extends BaseRepository<Question> {

    @Override
    public Stream<Question> find(Question pattern) {
        return map.values()
                .stream()
                .filter(question -> nullOrEquals(pattern.getId(), question.getId()))
                .filter(question -> nullOrEquals(pattern.getQuestId(), question.getQuestId()))
                .filter(question -> nullOrEquals(pattern.getText(), question.getText()))
                .filter(question -> nullOrEquals(pattern.getGameState(), question.getGameState()));
    }


}
