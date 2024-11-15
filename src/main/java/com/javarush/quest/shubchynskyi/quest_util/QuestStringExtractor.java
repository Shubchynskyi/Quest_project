package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.result.LogicBlockResult;
import org.springframework.stereotype.Component;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Component
public class QuestStringExtractor {

    public LogicBlockResult getExtractedData(String currentLine) {
        String numberStr = extractNumber(currentLine);
        String data = extractData(currentLine);
        String mark = extractMark(currentLine, numberStr);
        int number = Integer.parseInt(numberStr);
        return new LogicBlockResult(number, data, mark);
    }

    private String extractMark(String currentLine, String number) {
        String mark = currentLine.replaceFirst(number, REGEX_EMPTY_STRING);
        if (mark.length() > 1) {
            mark = mark.substring(0, 2);
        }
        return mark.trim();
    }

    private String extractNumber(String string) {
        int endIndex = findNumberEndIndex(string);

        if (endIndex <= 0) {
            throw new AppException(INCORRECT_STRING_NUMBER);
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