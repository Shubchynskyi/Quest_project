package com.example.quest_project.util;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class QuestMarks {
    private final String QUESTION_MARK = ":";
    final String ANSWER_MARK = "<";
    private final String WIN_MARK = "+";
    private final String LOST_MARK = "-";

    public final List<String> QUESTION_MARKS = new ArrayList<>(List.of(
            QUESTION_MARK, WIN_MARK, LOST_MARK
    ));
}
