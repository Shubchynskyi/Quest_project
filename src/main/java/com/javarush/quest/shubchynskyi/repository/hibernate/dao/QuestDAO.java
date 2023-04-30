package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Quest;
import org.hibernate.SessionFactory;

public class QuestDAO extends GenericDAO<Quest> {
    public QuestDAO(SessionFactory sessionFactory) {
        super(Quest.class, sessionFactory);
    }
}
