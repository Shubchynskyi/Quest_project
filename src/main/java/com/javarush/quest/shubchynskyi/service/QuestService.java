package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.entity.*;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.quest_util.BlockTypeResolver;
import com.javarush.quest.shubchynskyi.quest_util.QuestParser;
import com.javarush.quest.shubchynskyi.quest_util.QuestValidator;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.javarush.quest.shubchynskyi.localization.ExceptionErrorMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestionService questionService;
    private final QuestRepository questRepository;
    private final UserService userService;
    private final QuestParser questParser;
    private final QuestValidator questValidator;
    private final BlockTypeResolver blockTypeResolver;
    private final Lock lock;
    private final ImageService imageService;
    private final AnswerService answerService;

    @Transactional
    public Quest create(String name, String text, String description, String authorId) {
        log.info("Creating quest with name: {}. Author ID : {}", name, authorId);
        if (questValidator.isQuestExist(name)) {
            log.warn("Quest with name {} already exists. Author ID : {}", name, authorId);
            throw new AppException(QUEST_NAME_ALREADY_EXISTS);
        }

        User author;
        try {
            author = userService.get(Long.parseLong(authorId))
                    .orElseThrow(() -> new AppException(USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            log.error("Invalid author ID format: {}", authorId, e);
            throw new AppException(INVALID_AUTHOR_ID_FORMAT);
        }

        if (questValidator.isQuestTextValid(text)) {
            Quest quest = Quest.builder()
                    .name(name)
                    .description(description)
                    .author(author)
                    .build();

            log.info("Parsing quest text for quest: {}", name);
            parseQuestFromTextWall(quest, text);

            return quest;
        } else {
            log.warn("Parsing error. Quest text is not valid for quest: {}. Author ID : {}", name, authorId);
            throw new AppException(QUEST_TEXT_NOT_VALID);
        }
    }

    public void update(Quest quest) {
        questRepository.save(quest);
    }

    @Transactional
    public void delete(Quest quest) {
        Collection<Question> questions = quest.getQuestions();
        imageService.deleteOldFiles(quest.getImage());
        for (Question question : questions) {
            questionService.delete(question);
        }

        User author = quest.getAuthor();
        List<Quest> quests = author.getQuests();
        quests.remove(quest);
        author.setQuests(quests);
        userService.update(author);

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
                Long nextQuestionId = questionsMapWithRawId.get(AnswerEntry.getValue()).getId();
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
        // TODO парсер может возвращать обьект, и из него потом извлекать поля (а не из массива)
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

        answerService.create(answer);
//   TODO заменил, проверить     answerRepository.save(answer);
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
