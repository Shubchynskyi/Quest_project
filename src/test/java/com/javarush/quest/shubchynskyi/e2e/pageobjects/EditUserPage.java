package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.USER_URL;

public class EditUserPage extends BasePage {

    @FindBy(id = "login")
    private WebElement loginField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "updateOrCreate")
    private WebElement saveButton;

    @FindBy(id = "delete")
    private WebElement deleteButton;

    public EditUserPage(WebDriver driver, int port) {
        super(driver, port);
        wait.until(ExpectedConditions.visibilityOf(loginField));
    }

    public boolean isOnEditUserPage() {
        wait.until(ExpectedConditions.urlContains(USER_URL));
        return getCurrentUrl().contains(USER_URL);
    }

    public void fillLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void fillPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public void clickDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
    }

}