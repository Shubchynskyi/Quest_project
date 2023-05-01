package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.repository.hibernate.SessionFactoryCreator;
import org.hibernate.SessionFactory;

public class QuestRepository extends GenericDAO<Quest> {
    public QuestRepository(SessionFactoryCreator sessionFactoryCreator) {
        super(Quest.class, sessionFactoryCreator);
    }
}
