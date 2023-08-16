package com.javarush.quest.shubchynskyi.entity;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.QuestMarksEnum;

public enum GameState {
    PLAY, WIN, LOST;

    public static GameState defineState(String unknownState) {
        for (QuestMarksEnum questMark : QuestMarksEnum.values()) {
            if (questMark.getMark().equals(unknownState)) {
                return switch (questMark) {
                    case PLAY -> PLAY;
                    case WIN -> WIN;
                    case LOST -> LOST;
                    default -> throw new AppException(Key.UNEXPECTED_VALUE + questMark);
                };
            }
        }
        throw new AppException(Key.UNKNOWN_GAME_STATE);
    }
}
