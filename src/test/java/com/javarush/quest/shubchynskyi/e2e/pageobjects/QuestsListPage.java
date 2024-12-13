package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.QUESTS_LIST_URL;

public class QuestsListPage extends BasePage {

    public QuestsListPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + QUESTS_LIST_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.className("card")));
    }

    public WebElement findQuestPlayButton(String questName) {
        List<WebElement> quests = driver.findElements(By.xpath("//h5[text()='" + questName + "']/ancestor::div[contains(@class, 'card')]"));
        if (quests.isEmpty()) return null;
        WebElement questCard = quests.getFirst();
        return questCard.findElement(By.cssSelector("a.btn-primary"));
    }
}