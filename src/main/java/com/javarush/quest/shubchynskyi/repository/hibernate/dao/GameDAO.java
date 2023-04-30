package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Game;
import org.hibernate.SessionFactory;

public class GameDAO extends GenericDAO<Game> {
    public GameDAO(SessionFactory sessionFactory) {
        super(Game.class, sessionFactory);
    }
}
