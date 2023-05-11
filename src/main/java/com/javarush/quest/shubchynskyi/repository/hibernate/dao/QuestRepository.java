package com.javarush.quest.shubchynskyi.repository.hibernate.dao;

import com.javarush.quest.shubchynskyi.config.SessionCreator;
import com.javarush.quest.shubchynskyi.entity.Quest;
import org.springframework.stereotype.Repository;

@Repository
public class QuestRepository extends GenericDAO<Quest> {
    public QuestRepository(SessionCreator sessionCreator) {
        super(Quest.class, sessionCreator);
    }
}
