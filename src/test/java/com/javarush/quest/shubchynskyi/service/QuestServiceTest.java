package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.BlockTypeResolver;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestValidator;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class QuestServiceTest {
    @Mock
    private QuestParser questParser;
    @Mock
    private BlockTypeResolver blockTypeResolver;
    @Mock
    private QuestionService questionService;
    @Mock
    private UserService userService;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestRepository questRepository;
    @Mock
    private QuestValidator questValidator;
    @Mock
    private Lock lock;
    @InjectMocks
    private QuestService questService;
    private static final Long USER_ID = 2L;
    private static final Long QUEST_ID = 1L;
    private static final String NOT_EXIST_QUEST_ID = "5";
    private static final String INVALID_QUEST_ID = "no id";
    private static final String QUEST_NAME = "Quest Name";
    private static final String QUEST_DESCRIPTION = "Quest Description";
    private static final String TEXT = "Quest text";
    private Quest testQuest;
    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = User.builder().id(USER_ID).build();

        testQuest = Quest.builder()
                .author(testUser)
                .id(QUEST_ID)
                .name(QUEST_NAME)
                .description(QUEST_DESCRIPTION)
                .build();
    }

    @Test
    void should_CreateQuest_When_ParametersAreValid() {
        when(questRepository.save(any())).thenReturn(testQuest);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(true);
        when(userService.get(USER_ID)).thenReturn(Optional.of(testUser));

        Quest createdQuest = questService.create(
                QUEST_NAME,
                TEXT,
                QUEST_DESCRIPTION,
                String.valueOf(USER_ID));

        assertEquals(QUEST_NAME, createdQuest.getName());
        assertEquals(QUEST_DESCRIPTION, createdQuest.getDescription());
        assertEquals(USER_ID, createdQuest.getAuthor().getId());

        verify(questRepository, times(2)).save(any());
        verify(lock, times(1)).lock();
        verify(lock, times(1)).unlock();
    }

    @Test
    public void should_ThrowAppException_When_QuestTextIsInvalid() {
        when(questValidator.isQuestExist(anyString())).thenReturn(false);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(false);
        when(userService.get(USER_ID)).thenReturn(Optional.of(testUser));

        assertThrows(AppException.class,
                () -> questService.create(
                QUEST_NAME,
                TEXT,
                QUEST_DESCRIPTION,
                String.valueOf(USER_ID))
        );

        verify(questValidator, times(1)).isQuestExist(any());
        verify(questValidator, times(1)).isQuestTextValid(any());
        verifyNoInteractions(questRepository);
    }

    @Test
    public void should_ThrowAppException_When_QuestNameAlreadyExists() {
        when(questValidator.isQuestExist(anyString())).thenReturn(true);

        assertThrows(AppException.class,
                () -> questService.create(
                        QUEST_NAME,
                        TEXT,
                        QUEST_DESCRIPTION,
                        String.valueOf(USER_ID))
        );
        verifyNoInteractions(questRepository);
    }

    @Test
    public void should_UpdateQuest_When_QuestIsValid() {
        questService.update(testQuest);
        verify(questRepository, times(1)).save(testQuest);
    }

    @Test
    public void should_DeleteQuest_When_QuestIsValid() {
        questService.delete(testQuest);
        verify(questRepository, times(1)).delete(testQuest);
    }

    @Test
    public void should_ReturnAllQuests_When_QuestsExist() {
        List<Quest> quests = Arrays.asList(testQuest, testQuest);
        when(questRepository.findAll()).thenReturn(quests);

        Collection<Optional<Quest>> results = questService.getAll();
        assertEquals(2, results.size());
    }

    @Test
    public void should_ReturnQuestById_When_QuestExists() {
        when(questRepository.findById(anyLong())).thenReturn(Optional.of(testQuest));

        Optional<Quest> result = questService.get(QUEST_ID);
        assertTrue(result.isPresent());
    }

    @Test
    public void should_ReturnQuestByStringId_When_QuestExists() {
        when(questRepository.findById(anyLong())).thenReturn(Optional.of(testQuest));

        Optional<Quest> result = questService.get(String.valueOf(QUEST_ID));
        assertTrue(result.isPresent());
    }

    @Test
    public void should_ReturnEmptyOptional_When_QuestDoesNotExist() {
        when(questRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Quest> result = questService.get(NOT_EXIST_QUEST_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    public void should_ThrowNumberFormatException_When_QuestIdIsInvalid() {
        assertThrows(NumberFormatException.class,
                () -> questService.get(INVALID_QUEST_ID)
        );
    }

}