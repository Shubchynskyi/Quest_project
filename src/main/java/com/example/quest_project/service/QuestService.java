package com.example.quest_project.service;

import com.example.quest_project.AppException;
import com.example.quest_project.config.Config;
import com.example.quest_project.entity.Answer;
import com.example.quest_project.entity.GameState;
import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.util.QuestParser;


import java.util.*;

import static com.example.quest_project.util.QuestMarks.*;

public enum QuestService {

    QUEST_SERVICE;


    private final Config config = Config.CONFIG;
    private final QuestParser questParser = QuestParser.QUEST_PARSER;
    private final QuestionService questionService = QuestionService.QUESTION_SERVICE;
//    private final AnswerService answerService = AnswerService.ANSWER_SERVICE;


    public void create(Quest quest) {
        config.questRepository.create(quest);
    }

    public void update(Quest quest) {
        config.questRepository.update(quest);
    }

    public void delete(Quest quest) {
        config.questRepository.delete(quest);
    }

    public void parseQuestFromTextWall(String title, String text, Long authorId) {

        Map<Integer, Question> questionsMapWithRawId = new HashMap<>();
        Map<Answer, Integer> answersMapWithNullNextQuestionId = new HashMap<>();
        Collection<Answer> answers = new ArrayList<>();

        Quest quest = Quest.builder()
                .name(title)
                .text(text)
                .authorId(authorId)
                .startQuestionId(null) // если после вопроса лист строк пустой, то заполняю последний вопросом
                .build();

        create(quest);
//        System.out.println(quest.getId());


//        System.out.println(quest);
//        System.out.println();

        questParser.splitQuestToStrings(text);

        // это будет метод в цикле while(пока есть строки)
        while (questParser.isStringPresent()) {
            String currentLine = questParser.takeNextLine();
            String[] logicBlock = questParser.extractLogicBlock(currentLine);
            Integer blockNumber = Integer.valueOf(logicBlock[0]);
            String blockData = logicBlock[1];
            String blockType = logicBlock[2]; // тут метка

//            System.out.println("Номер блока: " + blockNumber);
//            System.out.println("Данные блока: " + blockData);
//            System.out.println("Метка: " + blockType);

            switch (blockType) {
                case PLAY, WIN, LOST -> {
                    Question question = Question.builder()
                            .questId(quest.getId())
                            .text(blockData)
                            .gameState(GameState.getState(blockType))
                            .build();

                    question.getAnswers().addAll(answers);
                    answers.clear();
                    questionService.create(question);
                    question.getAnswers().forEach(answer -> answer.setQuestionId(question.getId()));
                    quest.getQuestions().add(question);


                    System.out.println(blockNumber);
                    System.out.println(question);
                    questionsMapWithRawId.put(blockNumber, question);
                    System.out.println(questionsMapWithRawId);
                    System.out.println();




//                    System.out.println(question);

                    // если нет больше строк, то задаем первый вопрос квеста и отчищаем карту вопросов в памяти
                    if (!questParser.isStringPresent()) {
                        quest.setStartQuestionId(question.getId());
                    }
                }
                case ANSWER -> {
                    Answer answer = Answer.builder()
                            .text(blockData)
//                            .nextQuestionId(questionsMapWithRawId.get(blockNumber).getId())
                            .build();


                    // если вопроса, на который ведет текущий ответ, еще нет
                    // то помещаем ответ без ссылки в мапу и по окончании работы цикла будет сопоставление


                    if (questionsMapWithRawId.containsKey(blockNumber)) {
                        answer.setNextQuestionId(questionsMapWithRawId.get(blockNumber).getId());
                    } else {
                        answersMapWithNullNextQuestionId.put(answer, blockNumber);
                    }

                    config.answerRepository.create(answer);
                    answers.add(answer);
                }
                default -> throw new AppException(); //TODO неверная метка
            }





        }
        // тут нужно присвоить ответам, у которых ссылка на вопрос = Null, актуальные ссылки
        // и потом очистить обе мапы
        for (Map.Entry<Answer, Integer> integerAnswerEntry : answersMapWithNullNextQuestionId.entrySet()) {

            System.out.println(integerAnswerEntry);
            System.out.println();
            System.out.println(questionsMapWithRawId);
            System.out.println();

            integerAnswerEntry.getKey()
                    .setNextQuestionId(
                            questionsMapWithRawId.get(integerAnswerEntry.getValue()).getId()
                    );
        }

        questionsMapWithRawId.clear();
        answersMapWithNullNextQuestionId.clear();
        System.out.println("Квест:");
        System.out.println(quest);
        System.out.println();
        System.out.println("Квест репо:");
        System.out.println(config.questRepository.getAll());
        System.out.println();
        System.out.println("Вопрос репо:");
        System.out.println(config.questionRepository.getAll());
        System.out.println();
        System.out.println("Ответ репо:");
        System.out.println(config.answerRepository.getAll());
        System.out.println();
        System.out.println();

    }


}
