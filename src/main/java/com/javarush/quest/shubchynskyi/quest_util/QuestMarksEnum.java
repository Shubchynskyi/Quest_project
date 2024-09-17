package com.javarush.quest.shubchynskyi.quest_util;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    static List<String> getAllMarks() {
        return Stream.of(QuestMarksEnum.values())
                .map(QuestMarksEnum::getMark)
                .collect(Collectors.toList());
    }
}