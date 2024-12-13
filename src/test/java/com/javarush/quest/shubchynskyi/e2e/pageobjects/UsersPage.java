package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.USERS_URL;

public class UsersPage extends BasePage {

    @FindBy(css = "div.card")
    private List<WebElement> userCards;

    public UsersPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + USERS_URL);
        waitForPageToLoad();
    }

    public List<WebElement> getUserCards() {
        wait.until(ExpectedConditions.visibilityOfAllElements(userCards));
        return driver.findElements(By.cssSelector("div.card"));
    }

    public void clickEditButton(WebElement userCard) {
        WebElement editButton = userCard.findElement(By.cssSelector("a.btn-primary"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", editButton);
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
    }

    public void clickDeleteButton(WebElement userCard) {
        WebElement deleteButton = userCard.findElement(By.cssSelector("button.btn-danger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.card")));
    }
}