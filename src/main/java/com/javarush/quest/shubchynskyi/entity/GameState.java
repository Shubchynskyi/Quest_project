package com.javarush.quest.shubchynskyi.entity;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.util.QuestMarks;

public enum GameState {
    PLAY, WIN, LOST;

    public static GameState defineState(String unknownState) {
        return switch (unknownState) {
            case QuestMarks.PLAY -> PLAY;
            case QuestMarks.WIN -> WIN;
            case QuestMarks.LOST -> LOST;
            default -> throw new AppException();
        };
    }
}
