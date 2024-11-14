package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.exception.AppException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestStringExtractorTest {

    private final QuestStringExtractor extractor = new QuestStringExtractor();

    @Test
    void should_ReturnCorrectData_When_StringIsValid() {
        String input = "1+ Answer";
        String[] extractedData = extractor.getExtractedData(input);

        assertEquals("1", extractedData[0]);
        assertEquals("Answer", extractedData[1]);
        assertEquals("+", extractedData[2]);
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