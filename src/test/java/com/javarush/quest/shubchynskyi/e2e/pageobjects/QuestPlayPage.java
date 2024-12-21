package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class QuestPlayPage extends BasePage {

    public QuestPlayPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public boolean isStartButtonVisible() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("start")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickStart() {
        WebElement startButton = driver.findElement(By.id("start"));
        startButton.click();
    }

    public boolean isQuestionVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[not(@class)]")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void selectFirstAnswer() {
        WebElement firstAnswer = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='radio']")));
        firstAnswer.click();
    }

    public void clickNext() {
        WebElement nextButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("singlebutton")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
    }

    public boolean isGameFinished() {
        try {
            WebElement gameState = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'To Quest List')]")));
            if (gameState != null) {
                gameState.click();
                return true;
            }
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }
}