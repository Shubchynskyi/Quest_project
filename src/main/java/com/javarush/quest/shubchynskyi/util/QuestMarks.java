package com.javarush.quest.shubchynskyi.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class QuestMarks {
    public final String PLAY = ":";
    public final String ANSWER = "<";
    public final String WIN = "+";
    public final String LOST = "-";

    public final List<String> QUESTION_MARKS = new ArrayList<>(List.of(
            PLAY, WIN, LOST
    ));
}
