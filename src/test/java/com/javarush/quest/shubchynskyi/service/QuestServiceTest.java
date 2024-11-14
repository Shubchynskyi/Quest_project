package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestValidator;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.locks.Lock;

import static com.javarush.quest.shubchynskyi.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestServiceTest {
    @Mock
    private QuestParser questParser;
    @Mock
    private UserService userService;
    @Mock
    private QuestRepository questRepository;
    @Mock
    private QuestValidator questValidator;
    @Mock
    private ImageService imageService;
    @Mock
    private Lock lock;
    @InjectMocks
    private QuestService questService;

    private Quest testQuest;
    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = User.builder().id(USER_ID).build();

        testQuest = Quest.builder()
                .author(testUser)
                .id(QUEST_ID)
                .name(QUEST_NAME_HOLDER)
                .description(QUEST_DESCRIPTION_HOLDER)
                .build();

        ArrayList<Quest> quests = new ArrayList<>();
        quests.add(testQuest);
        testUser.setQuests(quests);
    }

    @Test
    void should_CreateQuest_When_ParametersAreValid() {
        when(questRepository.save(any())).thenReturn(testQuest);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(true);
        when(userService.get(USER_ID)).thenReturn(Optional.of(testUser));

        Quest createdQuest = questService.create(
                QUEST_NAME_HOLDER,
                QUEST_TEXT_HOLDER,
                QUEST_DESCRIPTION_HOLDER,
                String.valueOf(USER_ID));

        assertEquals(QUEST_NAME_HOLDER, createdQuest.getName());
        assertEquals(QUEST_DESCRIPTION_HOLDER, createdQuest.getDescription());
        assertEquals(USER_ID, createdQuest.getAuthor().getId());

        verify(questRepository, times(2)).save(any());
        verify(lock, times(1)).lock();
        verify(lock, times(1)).unlock();
        verify(questParser).splitQuestToStrings(anyString());
    }

    @Test
    public void should_ThrowAppException_When_QuestTextIsInvalid() {
        when(questValidator.isQuestExist(anyString())).thenReturn(false);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(false);
        when(userService.get(USER_ID)).thenReturn(Optional.of(testUser));

        assertThrows(AppException.class,
                () -> questService.create(
                        QUEST_NAME_HOLDER,
                        QUEST_TEXT_HOLDER,
                        QUEST_DESCRIPTION_HOLDER,
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
                        QUEST_NAME_HOLDER,
                        QUEST_TEXT_HOLDER,
                        QUEST_DESCRIPTION_HOLDER,
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
        verify(imageService, times(1)).deleteOldFiles(testQuest.getImage());
        verify(userService, times(1)).update(any());
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