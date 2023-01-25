package com.example.quest_project.util;

import com.example.quest_project.AppException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.quest_project.util.QuestMarks.ANSWER_MARK;
import static com.example.quest_project.util.QuestMarks.QUESTION_MARKS;

public enum QuestParser {
    QUEST_PARSER;

    private List<String> stringList;

    public String takeNextLine() {
        return stringList.remove(0);
    }

    public boolean isStringPresent() {
        return stringList.size() > 0;
    }

    public String extractNumber(String string) {    //994- Два
        String number;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                number = string.substring(0, i - 1).trim();
                return number;
            }
        }
        throw new AppException(); //TODO строка не корректная, нет номера
    }

    public String extractData(String string) {
        String result;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                result = string.substring(i).trim();
                return result;
            }
        }
        throw new AppException(); //TODO строка не корректная
    }

    //TODO
    public String fillData(String currentLine) {
        StringBuilder currentLineBuilder = new StringBuilder(currentLine);
        do {
            String nextLine = takeNextLine();
            currentLineBuilder.insert(0, nextLine + "\n");
            if (!isStringWithOutMark(nextLine)) {
                break;
            }
        } while (isStringPresent());
        String result = currentLineBuilder.toString();
        if (isStringWithOutMark(result)) {
            throw new RuntimeException(); //TODO - если строки кончились, но блок не собран
        }
        return result;
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
            currentLine = fillData(currentLine);
        }
        result[0] = extractNumber(currentLine);
        result[1] = extractData(currentLine);
        result[2] = extractMark(currentLine, result[0], result[1]);
        return result;
    }

    private String extractMark(String currentLine, String number, String data) {
        String mark = currentLine.replaceFirst(number, "");
        mark = mark.substring(0, 2);
        return mark.trim();
    }
}
