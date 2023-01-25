package com.example.quest_project.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.example.quest_project.util.QuestMarks.*;

public enum QuestParser {
    QUEST_PARSER;


    private List<String> stringList;

    public String takeNextLine() {
        return stringList.remove(0);
    }

    public String getNextLine() {
        return stringList.get(0);
    }

    public String extractNumber(String string) {
        String number = null;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                number = string.substring(0, i - 1).trim();
            }
        }
        return number;
    }

    public String extractData(String string) {
        String result = null;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                result = string.substring(i).trim();
            }
        }
        return result;
    }


    public String fillData(String currentLine) {    // беру нижнюю строку, в ней нет меток
        String nextLine = takeNextLine();           // читаю след. строку
        currentLine = nextLine + currentLine;       // теперь текущая состоит из двух (верхняя + нижняя)
        if(isStringWithOutMark(getNextLine())) {    // если в следующей строке нет меток, то повторяем
            fillData(currentLine);
        }
        return currentLine;   // возвращаем результат (должен быть готовый блок, номер + вопрос или ответ)
    }

    public boolean isStringWithOutMark(String string) {
        return (!isStringQuestion(string) && !isStringAnswer(string));
    }

    public boolean isStringAnswer(String string) {
        int markIndex = string.indexOf(ANSWER_MARK);
        return markIndex > 0 && StringUtils.isNumeric(String.valueOf(string.charAt(markIndex - 1)));
    }

    public boolean isStringQuestion(String string) {
        for (String answerMark : QUESTION_MARKS) {
            int markIndex = string.indexOf(answerMark);
            if (markIndex > 0 && StringUtils.isNumeric(String.valueOf(string.charAt(markIndex - 1)))) {
                return true;
            }
        }
        return false;
    }

    public void splitQuestToStrings(String text) {
        List<String> result = new ArrayList<>(Arrays
                .stream(text.split("\n"))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .toList());
        Collections.reverse(result);
        stringList = result;
    }



    public String[] extractLogicBlock(String currentLine) {
        String[] result = new String[3];
        if (isStringWithOutMark(currentLine)) {
            // логика для пустой строки (без меток)
            currentLine = fillData(currentLine);
        }
        result[0] = extractNumber(currentLine);
        result[1] = extractData(currentLine);
        result[2] = extractMark(currentLine);
        return result;
    }

    private String extractMark(String currentLine) {
        // TODO logic
        return null;
    }
}
