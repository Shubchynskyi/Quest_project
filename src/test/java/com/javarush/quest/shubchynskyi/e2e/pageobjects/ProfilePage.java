package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.PROFILE_URL;

public class ProfilePage extends BasePage {

    @FindBy(xpath = "//h5[contains(text(), 'Login:')]")
    private WebElement loginTextElement;

    @FindBy(xpath = "//h5[contains(text(), 'Role:')]")
    private WebElement roleTextElement;

    @FindBy(name = "user")
    private WebElement editUserButton;

    @FindBy(className = "card")
    private List<WebElement> questCards;


    public ProfilePage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + PROFILE_URL);
        wait.until(ExpectedConditions.visibilityOf(loginTextElement));
    }

    public String getLoginText() {
        return loginTextElement.getText();
    }

    public String getRoleText() {
        return roleTextElement.getText();
    }

    public void clickEditUserButton() {
        wait.until(ExpectedConditions.elementToBeClickable(editUserButton)).click();
    }

    public List<WebElement> getQuestCards() {
        wait.until(ExpectedConditions.visibilityOfAllElements(questCards));
        return questCards;
    }

    public void clickEditQuestButtonForFirstQuest() {
        WebElement firstEditButton = driver.findElement(By.cssSelector(".btn-secondary"));
        wait.until(ExpectedConditions.elementToBeClickable(firstEditButton)).click();
    }

    public boolean isOnProfilePage() {
        wait.until(ExpectedConditions.urlContains(PROFILE_URL));
        return getCurrentUrl().contains(PROFILE_URL);
    }

    public void clickDeleteQuestButtonForFirstQuest() {
        WebElement firstDeleteButton = driver.findElement(By.cssSelector(".btn-danger"));
        wait.until(ExpectedConditions.elementToBeClickable(firstDeleteButton)).click();
    }

    public boolean isDeleteConfirmationDisplayed() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public Alert getDeleteConfirmationAlert() {
        return driver.switchTo().alert();
    }
}