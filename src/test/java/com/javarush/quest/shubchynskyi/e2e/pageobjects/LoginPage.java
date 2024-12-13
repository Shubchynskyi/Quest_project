package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.LOGIN_URL;

public class LoginPage extends BasePage {

    @FindBy(id = "userLogin")
    private WebElement loginField;

    @FindBy(id = "userPassword")
    private WebElement passwordField;

    @FindBy(id = "submit")
    private WebElement submitButton;

    @FindBy(className = "alert-danger")
    private WebElement errorAlert;

    public LoginPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + LOGIN_URL);
        wait.until(ExpectedConditions.visibilityOf(loginField));
    }

    public void enterLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        submitButton.click();
    }

    public boolean isOnLoginPage() {
        return getCurrentUrl().contains(LOGIN_URL);
    }

    public boolean isErrorDisplayed() {
        return !driver.findElements(org.openqa.selenium.By.className("alert-danger")).isEmpty();
    }

    public String getErrorMessage() {
        if (errorAlert != null) {
            return errorAlert.getText();
        }
        return "";
    }
}