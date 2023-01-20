package com.example.quest_project.repository;

import com.example.quest_project.entity.Quest;


import java.util.stream.Stream;

/**
 * repository for quests, must be changed in future
 */
public class QuestRepository extends BaseRepository<Quest> {

//    public QuestRepository() {
//        // id -1L потом обновится базой данных на нормальный
//        create(new quest(-1L, "admin", "admin", Role.ADMIN));  
//        create(new quest(-1L, "guest", "guest", Role.GUEST));
//        create(new quest(-1L, "moderator", "moderator", Role.MODERATOR));
//        create(new quest(-1L, "quest1", "quest1", Role.quest));
//        create(new quest(-1L, "quest2", "quest2", Role.quest));
//        create(new quest(-1L, "quest3", "quest3", Role.quest));
//    }

    @Override
    public Stream<Quest> find(Quest pattern) {     // будет возвращать квесты которые соответствуют паттерну
        return map.values()
                .stream()
                .filter(quest -> nullOrEquals(pattern.getId(), quest.getId()))
                .filter(quest -> nullOrEquals(pattern.getText(), quest.getText()))
                .filter(quest -> nullOrEquals(pattern.getName(), quest.getName()))
                .filter(quest -> nullOrEquals(pattern.getAuthorId(), quest.getAuthorId()))
                .filter(quest -> nullOrEquals(pattern.getStartQuestionId(), quest.getStartQuestionId()));
    }

}
