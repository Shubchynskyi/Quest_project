package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Game;
import com.javarush.quest.shubchynskyi.repository.hibernate.SessionFactoryCreator;


public class GameRepository extends GenericDAO<Game> {
    public GameRepository(SessionFactoryCreator sessionFactoryCreator) {
        super(Game.class, sessionFactoryCreator);
    }
}
