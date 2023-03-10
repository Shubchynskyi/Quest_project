package com.javarush.quest.shubchynskyi.repository;


import com.javarush.quest.shubchynskyi.entity.game.Game;
import com.javarush.quest.shubchynskyi.repository.abstract_repo.BaseRepository;

import java.util.stream.Stream;

/**
 * repository for Games, must be changed in future
 */

@SuppressWarnings("unused")
public class GameRepository extends BaseRepository<Game> {

    @Override
    public Stream<Game> find(Game pattern) {
        return map.values()
                .stream()
                .filter(game -> nullOrEquals(pattern.getId(), game.getId()))
                .filter(game -> nullOrEquals(pattern.getQuestId(), game.getQuestId()))
                .filter(game -> nullOrEquals(pattern.getQuestId(), game.getQuestId()))
                .filter(game -> nullOrEquals(pattern.getCurrentQuestionId(), game.getCurrentQuestionId()))
                .filter(game -> nullOrEquals(pattern.getGameState(), game.getGameState()));
    }

}
