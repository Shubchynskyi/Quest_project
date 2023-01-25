package com.example.quest_project.service;

import com.example.quest_project.AppException;
import com.example.quest_project.entity.GameState;
import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.repository.QuestRepository;
import com.example.quest_project.repository.Repository;
import com.example.quest_project.util.QuestParser;

import java.util.*;

import static com.example.quest_project.util.QuestMarks.*;

public enum QuestService {

    QUEST_SERVICE;

    private final HashMap<Integer, String> questionMap = new HashMap<>(); // карта вопросов с номерами из текста

    private final QuestParser questParser = QuestParser.QUEST_PARSER;

    private final Repository<Quest> questRepository = new QuestRepository();

    public void create(Quest quest) {
        questRepository.create(quest);
    }

    public void update(Quest quest) {
        questRepository.update(quest);
    }

    public void delete(Quest quest) {
        questRepository.delete(quest);
    }

    public Quest parseQuestFromTextWall(String title, String text, Long authorId) {

        Map<Integer, Question> playQuestionsMap = new HashMap<>();
        Collection<Question> questions = new ArrayList<>();

        Quest quest = Quest.builder()
                .name(title)
                .text(text)
                .authorId(authorId)
                .startQuestionId(null) // если после вопроса лист строк пустой, то заполняю последний вопросом
                .build();

        System.out.println(quest.toString());
        System.out.println();

        //TODO заинжектить GAME_STATE и все репо
        questParser.splitQuestToStrings(text);

        // это будет метод в цикле while(пока есть строки)
        while(questParser.isStringPresent()) {
            String currentLine = questParser.takeNextLine();
            String[] logicBlock = questParser.extractLogicBlock(currentLine);
            String blockNumber = logicBlock[0];
            String blockData = logicBlock[1];
            String blockType = logicBlock[2]; // тут метка

            System.out.println("Номер блока: " + blockNumber);
            System.out.println("Данные блока: " + blockData);
            System.out.println("Метка: " + blockType);

            switch (blockType) {
                case QUESTION_MARK -> {
                    System.out.println(":");


                }
                case WIN_MARK -> {
                    System.out.println("+");


                }
                case LOST_MARK -> {
                    System.out.println("-");


                }
                case ANSWER_MARK -> {
                    System.out.println("<");


                }

                default -> throw new AppException(); //TODO неверная метка
            }
        }


//        в зависимости от типа метки делаю
        // : - вопрос с ответами, статус PLAY, заливаю ответы, обнуляю список ответов
        // < - ответ - пишу в лист, ищю id следующего вопроса в мапе вопросов
        // - - вопрос со статусом LOSE, добавляю в карту
        // + - вопрос со статусом WIN, добавляю в карту

        // проверить есть ли еще строки
        // если нет, то это будет стартовый вопрос - записать id в квест и выйти из цикла

        return null;
    }


}
