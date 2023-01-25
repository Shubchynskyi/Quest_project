package com.example.quest_project.service;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.repository.QuestRepository;
import com.example.quest_project.repository.Repository;
import com.example.quest_project.util.QuestParser;

import java.util.*;

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

        // это будет метод в цикле while(пока есть строки)
        questParser.splitQuestToStrings(text);

        String currentLine = questParser.takeNextLine();
        String[] logicBlock = questParser.extractLogicBlock(currentLine);
        String blockNumber = logicBlock[0];
        String blockData = logicBlock[1];
        String blackType = logicBlock[2]; // тут метка //TODO добавить в метод поиск метки, заинжектить GAME_STATE и все репо

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
