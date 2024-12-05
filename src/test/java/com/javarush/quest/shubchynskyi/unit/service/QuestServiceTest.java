package com.javarush.quest.shubchynskyi.unit.service;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestValidator;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import com.javarush.quest.shubchynskyi.service.ImageService;
import com.javarush.quest.shubchynskyi.service.QuestService;
import com.javarush.quest.shubchynskyi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.locks.Lock;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
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
        testUser = User.builder().id(TEST_USER_ID).build();

        testQuest = Quest.builder()
                .author(testUser)
                .id(TEST_QUEST_ID)
                .name(TEST_QUEST_NAME)
                .description(TEST_QUEST_DESCRIPTION)
                .build();

        ArrayList<Quest> quests = new ArrayList<>();
        quests.add(testQuest);
        testUser.setQuests(quests);
    }

    @Test
    void should_CreateQuest_When_ParametersAreValid() {
        when(questRepository.save(any())).thenReturn(testQuest);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(true);
        when(userService.get(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        Quest createdQuest = questService.create(
                TEST_QUEST_NAME,
                TEST_QUEST_TEXT,
                TEST_QUEST_DESCRIPTION,
                String.valueOf(TEST_USER_ID));

        assertEquals(TEST_QUEST_NAME, createdQuest.getName());
        assertEquals(TEST_QUEST_DESCRIPTION, createdQuest.getDescription());
        assertEquals(TEST_USER_ID, createdQuest.getAuthor().getId());

        verify(questRepository, times(2)).save(any());
        verify(lock, times(1)).lock();
        verify(lock, times(1)).unlock();
        verify(questParser).splitQuestToStrings(anyString());
    }

    @Test
    public void should_ThrowAppException_When_QuestTextIsInvalid() {
        when(questValidator.isQuestExist(anyString())).thenReturn(false);
        when(questValidator.isQuestTextValid(anyString())).thenReturn(false);
        when(userService.get(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        assertThrows(AppException.class,
                () -> questService.create(
                        TEST_QUEST_NAME,
                        TEST_QUEST_TEXT,
                        TEST_QUEST_DESCRIPTION,
                        String.valueOf(TEST_USER_ID))
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
                        TEST_QUEST_NAME,
                        TEST_QUEST_TEXT,
                        TEST_QUEST_DESCRIPTION,
                        String.valueOf(TEST_USER_ID))
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

        Optional<Quest> result = questService.get(TEST_QUEST_ID);
        assertTrue(result.isPresent());
    }

    @Test
    public void should_ReturnQuestByStringId_When_QuestExists() {
        when(questRepository.findById(anyLong())).thenReturn(Optional.of(testQuest));

        Optional<Quest> result = questService.get(String.valueOf(TEST_QUEST_ID));
        assertTrue(result.isPresent());
    }

    @Test
    public void should_ReturnEmptyOptional_When_QuestDoesNotExist() {
        when(questRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Quest> result = questService.get(NON_EXISTENT_QUEST_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    public void should_ThrowNumberFormatException_When_QuestIdIsInvalid() {
        assertThrows(NumberFormatException.class,
                () -> questService.get(INCORRECT_QUEST_ID)
        );
    }

    @Test
    public void should_ReturnAuthorId_When_QuestHasAuthor() {
        Long authorId = questService.getAuthorId(testQuest);

        assertNotNull(authorId);
        assertEquals(TEST_USER_ID, authorId);
    }

    @Test
    public void should_ReturnNull_When_QuestHasNoAuthor() {
        testQuest.setAuthor(null);

        Long authorId = questService.getAuthorId(testQuest);

        assertNull(authorId);
    }

}