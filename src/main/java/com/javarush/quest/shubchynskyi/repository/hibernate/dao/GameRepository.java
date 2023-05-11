package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Game;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository extends GenericDAO<Game> {
    public GameRepository(SessionCreator sessionCreator) {
        super(Game.class, sessionCreator);
    }
}
