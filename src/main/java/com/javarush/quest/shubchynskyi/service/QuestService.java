package com.javarush.quest.shubchynskyi.service;

import com.javarush.quest.shubchynskyi.entity.game.Answer;
import com.javarush.quest.shubchynskyi.config.Config;
import com.javarush.quest.shubchynskyi.entity.game.GameState;
import com.javarush.quest.shubchynskyi.entity.game.Quest;
import com.javarush.quest.shubchynskyi.entity.game.Question;
import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.util.Key;
import com.javarush.quest.shubchynskyi.util.QuestParser;

import java.util.*;

import static com.javarush.quest.shubchynskyi.util.QuestMarks.*;


public enum QuestService {

    QUEST_SERVICE;

    private final Config config = Config.CONFIG;
    private final QuestParser questParser = QuestParser.QUEST_PARSER;
    private final QuestionService questionService = QuestionService.QUESTION_SERVICE;

    public Quest create(String name, String text, String description, Long authorId) {
        Quest quest = Quest.builder()
                .name(name)
                .description(description)
                .authorId(authorId)
                .startQuestionId(-1L)
                .build();

        config.questRepository.create(quest);
        parseQuestFromTextWall(quest, text);

        return quest;
    }

    public void update(Quest quest) {
        config.questRepository.update(quest);
    }

    @SuppressWarnings("unused")
    public void delete(Quest quest) {
        config.questRepository.delete(quest);
    }

    public void parseQuestFromTextWall(Quest quest, String text) {
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

        questionsMapWithRawId.clear();
        answersMapWithNullNextQuestionId.clear();
        Collections.reverse((List<?>) quest.getQuestions());
        config.questRepository.update(quest);
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

        config.answerRepository.create(answer);
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
                .gameState(GameState.getStateFromParser(blockType))
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
        return config.questRepository.getAll();
    }

    @SuppressWarnings("unused")
    public Optional<Quest> get(Long id) {
        return Optional.ofNullable(config.questRepository.get(id));
    }

    public Optional<Quest> get(String id) {
        return Optional.ofNullable(config.questRepository.get(Long.parseLong(id)));
    }


}
