package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class QuestEditPage extends BasePage {

    @FindBy(id = "questName")
    private WebElement questNameField;

    @FindBy(id = "questDescription")
    private WebElement questDescriptionField;

    @FindBy(css = "form button.btn-success")
    private WebElement saveQuestButton;

    @FindBy(css = "button.btn-info")
    private WebElement toQuestsListButton;

    public QuestEditPage(WebDriver driver, int port) {
        super(driver, port);
        wait.until(ExpectedConditions.visibilityOf(questNameField));
    }

    public boolean isOnQuestEditPage() {
        return getCurrentUrl().contains("/quest-edit");
    }

    public void setQuestName(String name) {
        questNameField.clear();
        questNameField.sendKeys(name);
    }

    public void setQuestDescription(String description) {
        questDescriptionField.clear();
        questDescriptionField.sendKeys(description);
    }

    public String getQuestName() {
        return questNameField.getAttribute("value");
    }

    public String getQuestDescription() {
        return questDescriptionField.getAttribute("value");
    }

    public void clickSaveQuestButton() {
        wait.until(ExpectedConditions.elementToBeClickable(saveQuestButton));
        try {
            saveQuestButton.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveQuestButton);
        }
        wait.until(driver -> getCurrentUrl().contains("/quest-edit"));
    }

    public void clickToQuestsListButton() {
        wait.until(ExpectedConditions.elementToBeClickable(toQuestsListButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", toQuestsListButton);
        toQuestsListButton.click();
        wait.until(driver -> getCurrentUrl().contains("/profile") || getCurrentUrl().contains("/quests-list"));
    }
}