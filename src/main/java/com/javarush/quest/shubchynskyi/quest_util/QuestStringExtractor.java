package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.exception.AppException;
import org.springframework.stereotype.Component;

import static com.javarush.quest.shubchynskyi.constant.Key.INCORRECT_STRING;

@Component
public class QuestStringExtractor {

    // TODO return Object with 3 fields
    public String[] getExtractedData(String currentLine) {
        String[] result = new String[3];
        result[0] = extractNumber(currentLine);
        result[1] = extractData(currentLine);
        result[2] = extractMark(currentLine, result[0]);
        return result;
    }

    private String extractMark(String currentLine, String number) {
        String mark = currentLine.replaceFirst(number, Key.REGEX_EMPTY_STRING);
        if (mark.length() > 1) {
            mark = mark.substring(0, 2);
        }
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

        if (startIndex >= 1 && startIndex < string.length()) {
            String trimmedString = string.substring(startIndex).trim();
            if (!trimmedString.isEmpty()) {
                return trimmedString;
            }
        }

        throw new AppException(INCORRECT_STRING);
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
