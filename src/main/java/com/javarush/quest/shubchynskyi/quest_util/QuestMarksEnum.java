package com.javarush.quest.shubchynskyi.quest_util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum QuestMarksEnum {
    PLAY(":"),
    ANSWER("<"),
    WIN("+"),
    LOST("-");

    private final String mark;

    QuestMarksEnum(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public static List<String> getAllMarks() {
        return Stream.of(QuestMarksEnum.values())
                .map(QuestMarksEnum::getMark)
                .collect(Collectors.toList());
    }
}