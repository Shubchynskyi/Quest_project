package com.example.quest_project.repository;


import com.example.quest_project.entity.Game;

import java.util.stream.Stream;

/**
 * repository for Games, must be changed in future
 */
public class GameRepository extends BaseRepository<Game> {

    @Override
    public Stream<Game> find(Game pattern) {     // будет возвращать пользователей которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(game -> nullOrEquals(pattern.getId(), game.getId()))
                .filter(game -> nullOrEquals(pattern.getQuestId(), game.getQuestId()))
                .filter(game -> nullOrEquals(pattern.getQuestId(), game.getQuestId()))
                .filter(game -> nullOrEquals(pattern.getCurrentQuestionId(), game.getCurrentQuestionId()))
                .filter(game -> nullOrEquals(pattern.getGameState(), game.getGameState()));
    }

}
