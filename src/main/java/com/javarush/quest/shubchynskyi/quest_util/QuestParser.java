package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.exception.AppException;
import com.javarush.quest.shubchynskyi.result.LogicBlockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.javarush.quest.shubchynskyi.constant.Key.INCORRECT_TEXT_BLOCK;
import static com.javarush.quest.shubchynskyi.constant.Key.REGEX_NEW_LINE;

@Component
@RequiredArgsConstructor
public class QuestParser {

    public final QuestStringExtractor questStringExtractor;

    private List<String> stringList;

    public String takeNextLine() {
        return stringList.removeFirst();
    }

    public boolean isStringPresent() {
        return !stringList.isEmpty();
    }

    private String fillData(String currentLine) {
        StringBuilder currentLineBuilder = new StringBuilder(currentLine);
        do {
            String nextLine = takeNextLine();
            currentLineBuilder.insert(0, nextLine + REGEX_NEW_LINE);
            if (!isStringWithoutMark(nextLine)) {
                break;
            }
        } while (isStringPresent());
        String result = currentLineBuilder.toString();
        if (isStringWithoutMark(result)) {
            throw new AppException(INCORRECT_TEXT_BLOCK);
        }
        return result;
    }

    private boolean isStringWithoutMark(String string) {
        for (String mark : QuestMarksEnum.getAllMarks()) {
            int markIndex = string.indexOf(mark);
            if (markIndex > 0) {
                String charBeforeMark = String.valueOf(string.charAt(markIndex - 1));
                return !isNumeric(charBeforeMark);
            }
        }
        return true;
    }

    private boolean isNumeric(final CharSequence cs) {
        if (cs == null || cs.isEmpty()) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void splitQuestToStrings(String text) {
        List<String> result = new ArrayList<>(Arrays
                .stream(text.split(REGEX_NEW_LINE))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .toList());
        Collections.reverse(result);
        stringList = result;
    }

    public LogicBlockResult extractLogicBlock(String currentLine) {
        if (isStringWithoutMark(currentLine)) {
            currentLine = fillData(currentLine);
        }
        return questStringExtractor.getExtractedData(currentLine);
    }

}