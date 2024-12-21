package com.javarush.quest.shubchynskyi.entity;


import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.QuestMarksEnum;

import static com.javarush.quest.shubchynskyi.constant.Key.UNEXPECTED_VALUE;
import static com.javarush.quest.shubchynskyi.constant.Key.UNKNOWN_GAME_STATE;

public enum GameState {
    PLAY, WIN, LOST;

    public static GameState defineState(String unknownState) {
        for (QuestMarksEnum questMark : QuestMarksEnum.values()) {
            if (questMark.getMark().equals(unknownState)) {
                return switch (questMark) {
                    case PLAY -> PLAY;
                    case WIN -> WIN;
                    case LOST -> LOST;
                    default -> throw new AppException(UNEXPECTED_VALUE + questMark);
                };
            }
        }
        throw new AppException(UNKNOWN_GAME_STATE);
    }
}