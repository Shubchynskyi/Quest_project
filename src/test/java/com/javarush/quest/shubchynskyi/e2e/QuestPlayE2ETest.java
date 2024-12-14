package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.QuestsListPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.QuestPlayPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.QUESTS_LIST_URL;
import static org.junit.jupiter.api.Assertions.*;

public class QuestPlayE2ETest extends BaseE2ETest {

    @Test
    @DisplayName("Should play quest successfully")
    void shouldPlayQuestSuccessfully() {
        loginAsAdmin();
        QuestsListPage questsListPage = new QuestsListPage(driver, port);
        questsListPage.open();

        WebElement playButton = questsListPage.findQuestPlayButton("Before the magic stone"); //TODO
        assertNotNull(playButton, "Play button not found for the quest 'Before the magic stone'."); //TODO

        playButton.click();

        QuestPlayPage questPlayPage = new QuestPlayPage(driver, port);
        assertTrue(questPlayPage.isStartButtonVisible(), "Start button not found.");
        questPlayPage.clickStart();

        boolean isPlaying = true;
        while (isPlaying) {
            assertTrue(questPlayPage.isQuestionVisible(), "Question text is not displayed.");
            questPlayPage.selectFirstAnswer();
            questPlayPage.clickNext();
            if (questPlayPage.isGameFinished()) {
                isPlaying = false;
            }
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(QUESTS_LIST_URL), "Not redirected to /quests-list after finishing the quest.");
    }
}