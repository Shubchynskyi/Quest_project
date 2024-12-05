package com.javarush.quest.shubchynskyi.unit.quest_util;

import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestStringExtractor;
import com.javarush.quest.shubchynskyi.result.LogicBlockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestParserTest {

    @Mock
    private QuestStringExtractor questStringExtractor;
    @InjectMocks
    private QuestParser questParser;

    @BeforeEach
    void setUp() {
        questParser.splitQuestToStrings("1< Line1\n2< Line2\n3< Line3");
    }

    @Test
    void should_ReturnNextLine_When_TakeNextLineIsCalled() {
        assertEquals("3< Line3", questParser.takeNextLine());
    }

    @Test
    void should_ReturnTrue_When_IsStringPresent() {
        assertTrue(questParser.isStringPresent());
    }

    @Test
    void should_ReturnFalse_When_AllStringsAreTaken() {
        questParser.takeNextLine();
        questParser.takeNextLine();
        questParser.takeNextLine();
        assertFalse(questParser.isStringPresent());
    }

    @Test
    void should_ExtractLogicBlock_When_ExtractLogicBlockIsCalled() {
        when(questStringExtractor.getExtractedData(anyString()))
                .thenReturn(new LogicBlockResult(1, "Line1", "<"));

        LogicBlockResult result = questParser.extractLogicBlock("1< Line1");

        assertEquals(1, result.blockNumber());
        assertEquals("Line1", result.blockData());
        assertEquals("<", result.blockType());
    }

    @Test
    void should_ThrowException_When_NoMoreLinesToTake() {
        questParser.takeNextLine();
        questParser.takeNextLine();
        questParser.takeNextLine();
        assertThrows(NoSuchElementException.class, () -> questParser.takeNextLine());
    }

    @Test
    void should_CallQuestStringExtractor_When_ExtractLogicBlockIsCalled() {
        questParser.extractLogicBlock("1< Line1");
        verify(questStringExtractor, times(1)).getExtractedData(anyString());
    }

}