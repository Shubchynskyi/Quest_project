package com.javarush.quest.shubchynskyi.unit.quest_util;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.QuestStringExtractor;
import com.javarush.quest.shubchynskyi.result.LogicBlockResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestStringExtractorTest {

    private final QuestStringExtractor extractor = new QuestStringExtractor();

    @Test
    void should_ReturnCorrectData_When_StringIsValid() {
        String input = "1+ Answer";
        LogicBlockResult result = extractor.getExtractedData(input);

        assertEquals(1, result.blockNumber());
        assertEquals("Answer", result.blockData());
        assertEquals("+", result.blockType());
    }

    @Test
    void should_ThrowException_When_NumberIsMissing() {
        String input = "+ Answer";

        assertThrows(AppException.class, () -> extractor.getExtractedData(input));
    }

    @Test
    void should_ThrowException_When_DataIsMissing() {
        String input = "1+ ";

        assertThrows(AppException.class, () -> extractor.getExtractedData(input));
    }

}