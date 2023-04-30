package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Game;
import org.hibernate.SessionFactory;

public class GameRepository extends GenericDAO<Game> {
    public GameRepository(SessionFactory sessionFactory) {
        super(Game.class, sessionFactory);
    }
}
