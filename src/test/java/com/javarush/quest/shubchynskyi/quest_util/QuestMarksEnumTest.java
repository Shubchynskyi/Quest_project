package com.javarush.quest.shubchynskyi.quest_util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class QuestMarksEnumTest {

    @Test
    public void should_ReturnAllMarks_When_GetAllMarksIsCalled() {
        List<String> expectedMarks = Arrays.asList(":", "<", "+", "-");
        List<String> actualMarks = QuestMarksEnum.getAllMarks();
        assertEquals(expectedMarks, actualMarks);
    }

    @Test
    public void should_ReturnCorrectMark_When_GetMarkIsCalled() {
        assertEquals(":", QuestMarksEnum.PLAY.getMark());
        assertEquals("<", QuestMarksEnum.ANSWER.getMark());
        assertEquals("+", QuestMarksEnum.WIN.getMark());
        assertEquals("-", QuestMarksEnum.LOST.getMark());
    }
}