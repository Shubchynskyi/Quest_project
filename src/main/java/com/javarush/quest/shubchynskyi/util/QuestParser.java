package com.javarush.quest.shubchynskyi.util;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.util.constant.Key;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class QuestParser {

    private List<String> stringList;

    public String takeNextLine() {
        return stringList.remove(0);
        // TODO exception when invalid quest
    }

    public boolean isStringPresent() {
        return stringList.size() > 0;
    }

    private String extractNumber(String string) {
        String number;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                number = string.substring(0, i - 1).trim();
                return number;
            }
        }
        throw new AppException(Key.INCORRECT_STRING_NUMBER);
    }

    private String extractData(String string) {
        String result;
        for (int i = 1; i < string.length() + 1; i++) {
            if (!StringUtils.isNumeric(string.substring(0, i))) {
                result = string.substring(i).trim();
                return result;
            }
        }
        throw new AppException(Key.INCORRECT_STRING);
    }

    private String fillData(String currentLine) {
        StringBuilder currentLineBuilder = new StringBuilder(currentLine);
        do {
            String nextLine = takeNextLine();
            currentLineBuilder.insert(0, nextLine + Key.REGEX_NEW_LINE);
            if (!isStringWithOutMark(nextLine)) {
                break;
            }
        } while (isStringPresent());
        String result = currentLineBuilder.toString();
        if (isStringWithOutMark(result)) {
            throw new AppException(Key.INCORRECT_TEXT_BLOCK);
        }
        return result;
    }

    private boolean isStringWithOutMark(String string) {
        return (!isStringQuestion(string) && !isStringAnswer(string));
    }

    private boolean isStringAnswer(String string) {
        int markIndex = string.indexOf(QuestMarks.ANSWER);
        return markIndex > 0 && StringUtils.isNumeric(String.valueOf(string.charAt(markIndex - 1)));
    }

    private boolean isStringQuestion(String string) {
        for (String answerMark : QuestMarks.QUESTION_MARKS) {
            int markIndex = string.indexOf(answerMark);
            if (markIndex > 0 && StringUtils.isNumeric(String.valueOf(string.charAt(markIndex - 1)))) {
                return true;
            }
        }
        return false;
    }

    public void splitQuestToStrings(String text) {
        List<String> result = new ArrayList<>(Arrays
                .stream(text.split(Key.REGEX_NEW_LINE))
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
        result[2] = extractMark(currentLine, result[0]);
        return result;
    }

    private String extractMark(String currentLine, String number) {
        String mark = currentLine.replaceFirst(number, Key.REGEX_EMPTY_STRING);
        mark = mark.substring(0, 2);
        return mark.trim();
    }
}
