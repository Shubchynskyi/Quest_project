package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.entity.*;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.BlockTypeResolver;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestParser questParser;
    private final BlockTypeResolver blockTypeResolver;
    private final QuestionService questionService;
    private final QuestRepository questRepository;
    private final AnswerRepository answerRepository;
    private final Lock lock;

    @Transactional
    public Quest create(String name, String text, String description, String authorId) {

        Quest quest = Quest.builder()
                .name(name)
                .description(description)
                .authorId(User.builder().id(Long.valueOf(authorId)).build())
                .build();

        parseQuestFromTextWall(quest, text);

        return quest;
    }

    public void update(Quest quest) {
        questRepository.save(quest);
    }

    @SuppressWarnings("unused")
    public void delete(Quest quest) {
        questRepository.delete(quest);
    }

    @Transactional
    public void parseQuestFromTextWall(Quest quest, String text) {
        questRepository.save(quest);

        Optional<Quest> questWithId = questRepository.findAll(Example.of(quest)).stream().findAny();

        if (questWithId.isPresent()) {
            quest = questWithId.get();
        }
        lock.lock();
        try {
            Map<Integer, Question> questionsMapWithRawId = new HashMap<>();
            Map<Answer, Integer> answersMapWithNullNextQuestionId = new HashMap<>();
            Collection<Answer> answers = new ArrayList<>();

            questParser.splitQuestToStrings(text);

            while (questParser.isStringPresent()) {
                buildNewLogicBlock(quest, questionsMapWithRawId, answersMapWithNullNextQuestionId, answers);
            }

            for (var AnswerEntry : answersMapWithNullNextQuestionId.entrySet()) {
                Long nextQuestionId = questionsMapWithRawId
                        .get(AnswerEntry.getValue())
                        .getId();

                AnswerEntry.getKey().setNextQuestionId(nextQuestionId);
            }

            Collections.reverse((List<?>) quest.getQuestions());
            questRepository.save(quest);
        } finally {
            lock.unlock();
        }
    }

    private void buildNewLogicBlock(
            Quest quest,
            Map<Integer, Question> questionsMapWithRawId,
            Map<Answer, Integer> answersMapWithNullNextQuestionId,
            Collection<Answer> answers
    ) {

        String currentLine = questParser.takeNextLine();
        String[] logicBlock = questParser.extractLogicBlock(currentLine);
        Integer blockNumber = Integer.valueOf(logicBlock[0]);
        String blockData = logicBlock[1];
        String blockTypeStr = logicBlock[2];

        BlockTypeResolver.BlockType blockType = blockTypeResolver.defineBlockType(blockTypeStr);

        switch (blockType) {
            case PLAY, WIN, LOST ->
                    buildNewQuestion(quest, questionsMapWithRawId, answers, blockNumber, blockData, blockTypeStr);
            case ANSWER ->
                    buildNewAnswer(questionsMapWithRawId, answersMapWithNullNextQuestionId, answers, blockNumber, blockData);
            default -> throw new AppException(Key.INCORRECT_TYPE);
        }
    }

    private void buildNewAnswer(
            Map<Integer, Question> questionsMapWithRawId,
            Map<Answer, Integer> answersMapWithNullNextQuestionId,
            Collection<Answer> answers,
            Integer blockNumber, String blockData
    ) {

        Answer answer = Answer.builder()
                .text(blockData)
                .build();

        if (questionsMapWithRawId.containsKey(blockNumber)) {
            Long nextQuestionId = questionsMapWithRawId.get(blockNumber).getId();
            answer.setNextQuestionId(nextQuestionId);
        } else {
            answersMapWithNullNextQuestionId.put(answer, blockNumber);
        }

        answerRepository.save(answer);
        answers.add(answer);
    }

    private void buildNewQuestion(
            Quest quest,
            Map<Integer, Question> questionsMapWithRawId,
            Collection<Answer> answers,
            Integer blockNumber,
            String blockData,
            String blockType
    ) {

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

    public Collection<Optional<Quest>> getAll() {
        return questRepository.findAll().stream()
                .map(Optional::of)
                .collect(Collectors.toList());
    }

    public Optional<Quest> get(Long id) {
        return questRepository.findById(id);
    }

    public Optional<Quest> get(String id) {
        return get(Long.parseLong(id));
    }

}
