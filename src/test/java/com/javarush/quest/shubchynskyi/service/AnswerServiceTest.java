package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.TestConstants;
import com.javarush.quest.shubchynskyi.entity.Answer;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    private Answer testAnswer;

    @BeforeEach
    void setUp() {
        testAnswer = new Answer();
        testAnswer.setId(TestConstants.TEST_ANSWER_ID_HOLDER);
        testAnswer.setText(TestConstants.TEST_ANSWER_TEST_HOLDER);
    }

    @Test
    void should_SaveAnswer_When_CreateIsCalled() {
        answerService.create(testAnswer);

        verify(answerRepository, times(1)).save(testAnswer);
    }

    @Test
    void should_UpdateAnswer_When_UpdateIsCalled() {
        answerService.update(testAnswer);

        verify(answerRepository, times(1)).save(testAnswer);
    }

    @Test
    void should_DeleteAnswer_When_DeleteIsCalled() {
        answerService.delete(testAnswer);

        verify(answerRepository, times(1)).delete(testAnswer);
    }
}