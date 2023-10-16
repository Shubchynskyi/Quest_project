package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.entity.*;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.BlockTypeResolver;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestValidator;
import com.javarush.quest.shubchynskyi.repository.AnswerRepository;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

//TODO надо эти ошибки прокидывать в контроллер пользователю, т.е. их по сути надо тоже локализировать
import static com.javarush.quest.shubchynskyi.constant.Key.QUEST_TEXT_IS_NOT_VALID;
import static com.javarush.quest.shubchynskyi.constant.Key.QUEST_WITH_THIS_NAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestionService questionService;
    private final QuestRepository questRepository;
    private final AnswerRepository answerRepository;
    private final QuestParser questParser;
    private final QuestValidator questValidator;
    private final BlockTypeResolver blockTypeResolver;
    private final Lock lock;

    @Transactional
    public Quest create(
            String name,
            String text,
            String description,
            String authorId
    ) {
        if (questValidator.isQuestExist(name)) {
            throw new AppException(QUEST_WITH_THIS_NAME_ALREADY_EXISTS);
        }
        if (questValidator.isQuestTextValid(text)) {
            Quest quest = Quest.builder()
                    .name(name)
                    .description(description)
                    .authorId(User.builder().id(Long.valueOf(authorId)).build())
                    .build();

            parseQuestFromTextWall(quest, text);

            return quest;
        } else {
            throw new AppException(QUEST_TEXT_IS_NOT_VALID);
        }
    }

    public void update(Quest quest) {
        questRepository.save(quest);
    }

    @SuppressWarnings("unused")
    public void delete(Quest quest) {
        questRepository.delete(quest);
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


    @Transactional
    private void parseQuestFromTextWall(Quest quest, String text) {
        Quest questWithId = questRepository.save(quest);

        lock.lock();
        try {
            Map<Integer, Question> questionsMapWithRawId = new HashMap<>();
            Map<Answer, Integer> answersMapWithNullNextQuestionId = new HashMap<>();
            Collection<Answer> answers = new ArrayList<>();

            questParser.splitQuestToStrings(text);

            while (questParser.isStringPresent()) {
                buildNewLogicBlock(questWithId, questionsMapWithRawId, answersMapWithNullNextQuestionId, answers);
            }

            for (var AnswerEntry : answersMapWithNullNextQuestionId.entrySet()) {
                Long nextQuestionId = questionsMapWithRawId
                        .get(AnswerEntry.getValue())
                        .getId();

                AnswerEntry.getKey().setNextQuestionId(nextQuestionId);
            }

            Collections.reverse((List<Question>) questWithId.getQuestions());
            questRepository.save(questWithId);
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

}
