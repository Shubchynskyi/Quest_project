package com.javarush.quest.shubchynskyi.quest_util;

import com.javarush.quest.shubchynskyi.entity.Quest;
import com.javarush.quest.shubchynskyi.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.javarush.quest.shubchynskyi.constant.Key.*;

@Component
@RequiredArgsConstructor
public class QuestValidator {

    @Value("${app.quest.validator.minimum-question-in-quest}")
    private int minimumQuestionInQuest;
    @Value("${app.quest.validator.minimum-answer-in-quest}")
    private int minimumAnswerInQuest;
    @Value("${app.quest.validator.minimum-question-to-win-in-quest}")
    private int minimumQuestionToWinInQuest;
    @Value("${app.quest.validator.minimum-question-to-lose-in-quest}")
    private int minimumQuestionToLoseInQuest;

    private final QuestRepository questRepository;

    public boolean isQuestExist(String questName) {
        Quest questWithName = Quest.builder().name(questName).build();
        Optional<Quest> questOptional = questRepository
                .findAll(Example.of(questWithName))
                .stream().findAny();
        return questOptional.isPresent();
    }

    public boolean isQuestTextValid(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String[] lines = text.split(REGEX_NEW_LINE);
        if (lines.length < 5) {
            return false;
        }

        int questionCount = countMatches(lines, QUESTION_REGEX);
        int answerCount = countMatches(lines, ANSWER_REGEX);
        int winCount = countMatches(lines, WIN_REGEX);
        int loseCount = countMatches(lines, LOSE_REGEX);

        return questionCount >= minimumQuestionInQuest
                && answerCount >= minimumAnswerInQuest
                && winCount >= minimumQuestionToWinInQuest
                && loseCount >= minimumQuestionToLoseInQuest;
    }

    private int countMatches(String[] lines, String regex) {
        Pattern pattern = Pattern.compile(regex);
        int count = 0;
        for (String line : lines) {
            if (pattern.matcher(line).find()) {
                count++;
            }
        }
        return count;
    }
}
