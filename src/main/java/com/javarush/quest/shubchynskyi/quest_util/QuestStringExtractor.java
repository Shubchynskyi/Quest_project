package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import org.springframework.stereotype.Component;

@Component
public class QuestStringExtractor {

    String[] getExtractedData(String currentLine) {
        String[] result = new String[3];
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

    private String extractNumber(String string) {
        int endIndex = findNumberEndIndex(string);

        if (endIndex <= 0) {
            throw new AppException(Key.INCORRECT_STRING_NUMBER);
        }

        return string.substring(0, endIndex);
    }

    private String extractData(String string) {
        int startIndex = findNumberEndIndex(string) + 1;

        try {
            return string.substring(startIndex).trim();
        } catch (IndexOutOfBoundsException e) {
            throw new AppException(Key.INCORRECT_STRING);
        }
    }

    private int findNumberEndIndex(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
