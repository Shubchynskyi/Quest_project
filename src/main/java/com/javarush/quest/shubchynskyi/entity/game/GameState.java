package com.javarush.quest.shubchynskyi.entity.game;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.util.QuestMarks;

public enum GameState {
    PLAY, WIN, LOST;

    // TODO перенести в парсер
    public static GameState getStateFromParser(String stateFromParser) {
        return switch (stateFromParser) {
            case QuestMarks.PLAY -> PLAY;
            case QuestMarks.WIN -> WIN;
            case QuestMarks.LOST -> LOST;
            default -> throw new AppException();
        };
    }
}
