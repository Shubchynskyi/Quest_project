package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Quest;
import org.hibernate.SessionFactory;

public class QuestRepository extends GenericDAO<Quest> {
    public QuestRepository(SessionFactory sessionFactory) {
        super(Quest.class, sessionFactory);
    }
}
