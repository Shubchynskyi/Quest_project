package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestValidatorTest {


    @Mock
    private QuestRepository questRepository;

    @InjectMocks
    private QuestValidator questValidator;

    private static final String TEST_QUEST_NAME = "Test Quest";

    @Test
    void should_ReturnTrue_When_QuestExists() {
        Quest testQuest = Quest.builder().name(TEST_QUEST_NAME).build();
        List<Quest> quests = Collections.singletonList(testQuest);

        when(questRepository.findAll(ArgumentMatchers.<Example<Quest>>any())).thenReturn(quests);

        assertTrue(questValidator.isQuestExist(TEST_QUEST_NAME));
        verify(questRepository, times(1)).findAll(ArgumentMatchers.<Example<Quest>>any());
    }

    @Test
    void should_ReturnFalse_When_QuestDoesNotExist() {
        List<Quest> emptyQuestList = Collections.emptyList();

        when(questRepository.findAll(ArgumentMatchers.<Example<Quest>>any())).thenReturn(emptyQuestList);

        assertFalse(questValidator.isQuestExist(TEST_QUEST_NAME));
        verify(questRepository, times(1)).findAll(ArgumentMatchers.<Example<Quest>>any());
    }

    @Test
    void should_ReturnFalse_When_TextIsNull() {
        assertFalse(questValidator.isQuestTextValid(null));
    }

    @Test
    void should_ReturnFalse_When_TextIsEmpty() {
        assertFalse(questValidator.isQuestTextValid(""));
    }

    @Test
    void should_ReturnFalse_When_TextIsTooShort() {
        assertFalse(questValidator.isQuestTextValid("Question1\nAnswer1\nWin\nLose"));
    }

    @Test
    void should_ReturnTrue_When_TextIsValid() {
        String validText = """
                1: Test question
                2< Answer to won
                3< Answer to defeat

                3- Defeat

                2+ Won
                """;

        assertTrue(questValidator.isQuestTextValid(validText));
    }
}