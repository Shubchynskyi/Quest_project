package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.Game;
import org.hibernate.SessionFactory;

import java.util.stream.Stream;

public class GameDAO extends GenericDAO<Game> {
    public GameDAO(SessionFactory sessionFactory) {
        super(Game.class, sessionFactory);
    }


    @Override
    public Stream<Game> find(Game pattern) {
        return null;
    }
}
