package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.*;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.AnswerRepository;
import com.javarush.quest.shubchynskyi.repository.hibernate.dao.QuestRepository;
import com.javarush.quest.shubchynskyi.util.Key;
import com.javarush.quest.shubchynskyi.util.QuestParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.javarush.quest.shubchynskyi.util.QuestMarks.*;

@Service
public class QuestService {
    private QuestParser questParser;
    private QuestionService questionService;
    private QuestRepository questRepository;
    private AnswerRepository answerRepository;
    private final Lock lock = new ReentrantLock();

    @Autowired
    public void setQuestParser(QuestParser questParser) {
        this.questParser = questParser;
    }
    @Autowired
    public void setQuestionService(QuestionService questionService) {
        this.questionService = questionService;
    }
    @Autowired
    public void setQuestRepository(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }
    @Autowired
    public void setAnswerRepository(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Quest create(String name, String text, String description, Long authorId) {
        Quest quest = Quest.builder()
                .name(name)
                .description(description)
                .authorId(User.builder().id(authorId).build())
                .startQuestionId(-1L)
                .build();

        questRepository.create(quest);
        parseQuestFromTextWall(quest, text);

        return quest;
    }

    public void update(Quest quest) {
        questRepository.update(quest);
    }

    @SuppressWarnings("unused")
    public void delete(Quest quest) {
        questRepository.delete(quest);
    }

    @Transactional
    public void parseQuestFromTextWall(Quest quest, String text) {
        lock.lock();
        try {
            Map<Integer, Question> questionsMapWithRawId = new HashMap<>();
            Map<Answer, Integer> answersMapWithNullNextQuestionId = new HashMap<>();
            Collection<Answer> answers = new ArrayList<>();

            questParser.splitQuestToStrings(text);

            while (questParser.isStringPresent()) {
                buildNewLogicBlock(quest, questionsMapWithRawId, answersMapWithNullNextQuestionId, answers);
            }

            for (Map.Entry<Answer, Integer> integerAnswerEntry : answersMapWithNullNextQuestionId.entrySet()) {
                integerAnswerEntry.getKey()
                        .setNextQuestionId(questionsMapWithRawId.get(integerAnswerEntry.getValue()).getId());
            }

            Collections.reverse((List<?>) quest.getQuestions());
            questRepository.update(quest);
        } finally {
            lock.unlock();
        }
    }

    private void buildNewLogicBlock(
            Quest quest,
            Map<Integer, Question> questionsMapWithRawId,
            Map<Answer, Integer> answersMapWithNullNextQuestionId,
            Collection<Answer> answers) {

        String currentLine = questParser.takeNextLine();
        String[] logicBlock = questParser.extractLogicBlock(currentLine);
        Integer blockNumber = Integer.valueOf(logicBlock[0]);
        String blockData = logicBlock[1];
        String blockType = logicBlock[2];

        switch (blockType) {
            case PLAY, WIN, LOST ->
                    buildNewQuestion(quest, questionsMapWithRawId, answers, blockNumber, blockData, blockType);
            case ANSWER ->
                    buildNewAnswer(questionsMapWithRawId, answersMapWithNullNextQuestionId, answers, blockNumber, blockData);
            default -> throw new AppException(Key.INCORRECT_TYPE);
        }
    }

    private void buildNewAnswer(
            Map<Integer, Question> questionsMapWithRawId,
            Map<Answer, Integer> answersMapWithNullNextQuestionId,
            Collection<Answer> answers,
            Integer blockNumber, String blockData) {

        Answer answer = Answer.builder()
                .text(blockData)
                .build();

        if (questionsMapWithRawId.containsKey(blockNumber)) {
            answer.setNextQuestionId(questionsMapWithRawId.get(blockNumber).getId());
        } else {
            answersMapWithNullNextQuestionId.put(answer, blockNumber);
        }

        answerRepository.create(answer);
        answers.add(answer);
    }

    private void buildNewQuestion(
            Quest quest,
            Map<Integer, Question> questionsMapWithRawId,
            Collection<Answer> answers,
            Integer blockNumber, String blockData, String blockType) {

        Question question = Question.builder()
                .questId(quest.getId())
                .text(blockData)
                .gameState(GameState.defineState(blockType))
                .build();

        question.getAnswers().addAll(answers);
        answers.clear();
        questionService.create(question);
        question.getAnswers().forEach(answer -> answer.setQuestionId(question.getId()));
        quest.getQuestions().add(question);
        questionsMapWithRawId.put(blockNumber, question);

        if (!questParser.isStringPresent()) {
            quest.setStartQuestionId(question.getId());
        }
    }

    public Collection<Quest> getAll() {
        return questRepository.getAll();
    }

    @SuppressWarnings("unused")
    public Optional<Quest> get(Long id) {
        return Optional.ofNullable(questRepository.get(id));
    }

    public Optional<Quest> get(String id) {
        return Optional.ofNullable(questRepository.get(Long.parseLong(id)));
    }


}
