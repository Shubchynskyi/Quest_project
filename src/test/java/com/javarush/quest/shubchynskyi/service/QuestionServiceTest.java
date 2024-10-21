package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.Question;
import com.javarush.quest.shubchynskyi.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private AnswerService answerService;

    @InjectMocks
    private QuestionService questionService;

    private Question testQuestion;

    @BeforeEach
    void setUp() {
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setText("What is the capital of France?");
    }

    @Test
    void should_ReturnQuestion_When_QuestionExistsById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        assertEquals(Optional.of(testQuestion), questionService.get(1L));
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    void should_ReturnEmptyOptional_When_QuestionDoesNotExistById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), questionService.get(1L));
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    void should_SaveQuestion_When_CreateIsCalled() {
        questionService.create(testQuestion);

        verify(questionRepository, times(1)).save(testQuestion);
    }

    @Test
    void should_UpdateQuestion_When_UpdateIsCalled() {
        questionService.update(testQuestion);

        verify(questionRepository, times(1)).save(testQuestion);
    }

    @Test
    void should_DeleteQuestion_When_DeleteIsCalled() {
        questionService.delete(testQuestion);

        verify(questionRepository, times(1)).delete(testQuestion);
        verify(answerService, times(1)).deleteAll(anyCollection());
        verify(imageService, times(1)).deleteOldFiles(testQuestion.getImage());
    }
}
