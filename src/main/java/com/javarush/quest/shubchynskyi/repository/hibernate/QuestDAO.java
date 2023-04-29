package com.javarush.quest.shubchynskyi.repository.hibernate;

import com.javarush.quest.shubchynskyi.entity.Quest;
import org.hibernate.SessionFactory;

import java.util.stream.Stream;

public class QuestDAO extends GenericDAO<Quest> {
    public QuestDAO(SessionFactory sessionFactory) {
        super(Quest.class, sessionFactory);
    }

    @Override
    public Stream<Quest> find(Quest pattern) {
        return null;
    }
}
