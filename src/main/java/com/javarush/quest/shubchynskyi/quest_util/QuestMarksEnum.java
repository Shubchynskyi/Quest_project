package com.javarush.quest.shubchynskyi.quest_util;

import lombok.Getter;

@Getter
public enum QuestMarksEnum {
    PLAY(":"),
    ANSWER("<"),
    WIN("+"),
    LOST("-");

    private final String mark;

    QuestMarksEnum(String mark) {
        this.mark = mark;
    }

}