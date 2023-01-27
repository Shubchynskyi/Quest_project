package com.example.quest_project.entity;

import com.example.quest_project.AppException;
import com.example.quest_project.util.QuestMarks;

public enum GameState {
    PLAY, WIN, LOST;

    public static GameState getState(String stateFromParser) {
        return switch (stateFromParser) {
            case QuestMarks.PLAY -> PLAY;
            case QuestMarks.WIN -> WIN;
            case QuestMarks.LOST -> LOST;
            default -> throw new AppException(); //TODO wrong game state
        };
    }
}
